package com.nax.opcua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public class OpcUaStart {

    /**
    * @Description: start
    * @Param: []
    * @return: void
    * @Author: nax0305
    * @Date: 2024/5/28
    */
    public static void start() throws Exception {
        OcpuaClientUtil opcUaClientService = new OcpuaClientUtil();

        // 与OPC UA服务端建立连接，并返回客户端实例
        OpcUaClient client = opcUaClientService.connectOpcUaServer("10.226.96.210", "49320", "");

        // 遍历所有节点
        /*opcUaClientService.listNode(client, null);
        NodeId nodeId = new NodeId(2, "HB.1.SJ1.plcplb.plcwzlsd");*/

        // 读取指定节点的值
        opcUaClientService.readNodeValue(client, 2, "HB.1.SJ1.yqydgl.fmgd");
        /*opcUaClientService.readNodeValue(client, 2, "Demo.1500PLC.D2");*/

        // 向指定节点写入数据
        /*opcUaClientService.writeNodeValue(client, 2, "Demo.1500PLC.D1", 6f);*/

        // 订阅指定节点
        opcUaClientService.subscribe(client, 2, "HB.1.SJ1.yqydgl.fmgd");

        // 批量订阅多个节点
        /*List<String> identifiers = new ArrayList<>();
        identifiers.add("Demo.1500PLC.D1");
        identifiers.add("Demo.1500PLC.D2");

        opcUaClientService.setBatchNamespaceIndex(2);
        opcUaClientService.setBatchIdentifiers(identifiers);

        opcUaClientService.subscribeBatch(client);
        opcUaClientService.subscribeBatchWithReconnect(client);*/
    }

    public static void main(String[] args) {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
