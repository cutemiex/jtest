//package com.tiantiandou.serialization;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.protobuf.ByteString;
//import com.tiantiandou.serialization.UserProto.User;
//import com.tiantiandou.serialization.UserProto.User.Address;
//
///***
// * 类说明
// * 
// * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
// * @version 1.0
// * @since 2014年12月19日
// */
//public final class ProtoBufTest {
//    private static final Logger LOGGER = LoggerFactory.getLogger("ProtoBufTest");
//
//    private ProtoBufTest() {
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        User.Builder builder = User.newBuilder().addAddresses(
//                Address.newBuilder().addAddressName("tommy_1").addAddressName("Tommy_2")
//                        .setData(ByteString.copyFrom("Nothing".getBytes())).setId(1));
//        builder.addAddresses(Address.newBuilder().addAddressName("doudou_1").addAddressName("doudou_2")
//                .setData(ByteString.copyFrom("Any".getBytes())).setId(2));
//        builder.setName("user_name");
//        builder.setId(0);
//
//        byte[] serialized = builder.build().toByteArray();
//        User user = User.parseFrom(serialized);
//
//        LOGGER.debug(user.toString());
//    }
//}
