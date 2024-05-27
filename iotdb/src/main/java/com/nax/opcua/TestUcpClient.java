package com.nax.opcua;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

import java.util.List;

public class TestUcpClient {

    //opc ua服务端地址
//    private final static String endpointUrl = "opc.tcp://10.226.96.210:49320";

    public static void main(String[] args) throws Exception {

        String endpointUrl = "opc.tcp://milo.digitalpetri.com:62541/milo";

        OpcUaClient client = OpcUaClient.create(
                endpointUrl,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                configBuilder ->
                        configBuilder.build()
        );

        client.connect().get();

        List<EndpointDescription> endpoints =
                DiscoveryClient.getEndpoints(endpointUrl).get();

        for (EndpointDescription endpoint : endpoints){
            System.out.println(endpoint.getEndpointUrl() + "  " + endpoint.getServer());
        }

        // 地址空间
        AddressSpace addressSpace = client.getAddressSpace();

        UaNode serverNode = addressSpace.getNode(Identifiers.RootFolder);

        List<? extends UaNode> nodes = addressSpace.browseNodes(serverNode);

        for (UaNode node : nodes)
            System.out.println(node.getNodeId() + " " + node.getNodeClass() + " " + node.getBrowseName());

        AddressSpace.BrowseOptions browseOptions = AddressSpace.BrowseOptions.builder()
                .setReferenceType(Identifiers.HasProperty)
                .build();
        List<ReferenceDescription> references = nodes.get(0).browse(browseOptions);
    }

}
