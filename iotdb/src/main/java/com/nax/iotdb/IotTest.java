package com.nax.iotdb;

import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.isession.util.Version;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;

import java.nio.channels.SeekableByteChannel;

public class IotTest {

    public static Session session;

    public static void main(String[] args) throws IoTDBConnectionException, StatementExecutionException {
        /*session = new Session.Builder()
                .host("10.226.104.12")
                .port(6669)
                .username("root")
                .password("root")
                .build();*/

        session = new Session.Builder()
                .host("10.226.97.49")
                .port(6667)
                .username("root")
                .password("root")
                .build();
        session.open(false, 10000);

        System.out.println(session.checkTimeseriesExists("root.welink.var.`设备列表-干熄焦除尘空压风-流量`.prop.GXJCCKYF"));
        System.out.println("hahah");
        SessionDataSet dataSet = session.executeQueryStatement("select * from root.welink.var.`设备列表-干熄焦除尘空压风-流量`.prop.GXJCCKYF");
        System.out.println(dataSet.getFetchSize());
        session.close();

    }
}
