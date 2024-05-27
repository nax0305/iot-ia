package com.nax.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OcpuaClientTest {

    public static void main(String[] args) {

    }

    /**
     * 创建OPC UA客户端
     *
     * @param ip
     * @param port
     * @param suffix
     * @return
     * @throws Exception
     */
    public OpcUaClient connectOpcUaServer(String ip, String port, String suffix) throws Exception {
        String endPointUrl = "opc.tcp://" + ip + ":" + port + suffix;
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        OpcUaClient opcUaClient = OpcUaClient.create(endPointUrl,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                                .setApplicationUri("urn:eclipse:milo:examples:client")
                                //访问方式
                                .setIdentityProvider(new AnonymousProvider())
                                .setRequestTimeout(UInteger.valueOf(5000))
                                .build()
        );
        opcUaClient.connect().get();
        Thread.sleep(2000); // 线程休眠一下再返回对象，给创建过程一个时间。
        return opcUaClient;
    }

    /**
     * 遍历树形节点
     *
     * @param client OPC UA客户端
     * @param uaNode 节点
     * @throws Exception
     */
    public void listNode(OpcUaClient client, UaNode uaNode) throws Exception {
        List<? extends UaNode> nodes;
        if (uaNode == null) {
            nodes = client.getAddressSpace().browseNodes(Identifiers.ObjectsFolder);
        } else {
            nodes = client.getAddressSpace().browseNodes(uaNode);
        }
        for (UaNode nd : nodes) {
            //排除系统性节点，这些系统性节点名称一般都是以"_"开头
            String browseName = nd.getBrowseName().getName();
            if ('_' == browseName.charAt(0)) continue;
            System.out.println("Node= " + nd.getBrowseName().getName() + ", NodeId =" + nd.getNodeId());
            UShort index = nd.getNodeId().getNamespaceIndex();
            if (index.equals(UShort.valueOf(2)) )
                readNodeValue(client, 2, (String) nd.getNodeId().getIdentifier());
            listNode(client, nd);
        }
    }

    /**
     * 读取节点数据
     *
     * namespaceIndex可以通过UaExpert客户端去查询，一般来说这个值是2。
     * identifier也可以通过UaExpert客户端去查询，这个值=通道名称.设备名称.标记名称
     *
     * @param client
     * @param namespaceIndex
     * @param identifier
     * @throws Exception
     */
    public void readNodeValue(OpcUaClient client, int namespaceIndex, String identifier) throws Exception {
        //节点
        NodeId nodeId = new NodeId(namespaceIndex, identifier);

        //读取节点数据
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();

        // 状态
        System.out.println("Status: " + value.getStatusCode());

        //标识符
        String id = String.valueOf(nodeId.getIdentifier());
        System.out.println(id + ": " + value.getValue().getValue());
    }

    /**
     * 订阅(单个)
     *
     * @param client
     * @param namespaceIndex
     * @param identifier
     * @throws Exception
     */
    private static final AtomicInteger atomic = new AtomicInteger();

    public void subscribe(OpcUaClient client, int namespaceIndex, String identifier) throws Exception {
        //创建发布间隔1000ms的订阅对象
        client
                .getSubscriptionManager()
                .createSubscription(1000.0)
                .thenAccept(t -> {
                    //节点
                    NodeId nodeId = new NodeId(namespaceIndex, identifier);
                    ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                    //创建监控的参数
                    MonitoringParameters parameters = new MonitoringParameters(UInteger.valueOf(atomic.getAndIncrement()), 1000.0, null, UInteger.valueOf(10), true);
                    //创建监控项请求
                    //该请求最后用于创建订阅。
                    MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                    List<MonitoredItemCreateRequest> requests = new ArrayList<>();
                    requests.add(request);
                    //创建监控项，并且注册变量值改变时候的回调函数。
                    t.createMonitoredItems(
                            TimestampsToReturn.Both,
                            requests,
                            (item, id) -> item.setValueConsumer((it, val) -> {
                                System.out.println("nodeid :" + it.getReadValueId().getNodeId());
                                System.out.println("value :" + val.getValue().getValue());
                            })
                    );
                }).get();

        //持续订阅
        Thread.sleep(Long.MAX_VALUE);
    }



}
