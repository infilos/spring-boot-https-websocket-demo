package com.infilos.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Consts {

    public static final String CLIENT_ID_HEADER_NAME = "CLIENT_ID";

    /*1.Client Publish*/public static final String CLIENT_REQ_PUBLISH_TOPIC = "/server/req2server";
    /*2.Server Consume*/public static final String CLIENT_REQ_CONSUME_TOPIC = "/req2server";
    /*3.Server Publish*/public static final String SERVER_ACK_PUBLISH_TOPIC_PREFIX = "/server/ack2client/"/*+clientId*/;

    /*1.Server Publish*/public static final String SERVER_REQ_PUBLISH_TOPIC_PREFIX = "/server/req2client/"/*+clientId*/;
    /*2.Client Publish*/public static final String CLIENT_ACK_PUBLISH_TOPIC = "/server/ack2server";
    /*3.Server Consume*/public static final String CLIENT_ACK_CONSUME_TOPIC = "/ack2server";
}
