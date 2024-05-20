package com.sp.orange.utils;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;



import java.util.concurrent.CompletableFuture;

/**
 * 短信发送工具类
 */
public class SMSUtils {
//	/**
//	 * 发送短信
//	 * @param signName 签名
//	 * @param templateCode 模板
//	 * @param phoneNumbers 手机号
//	 * @param param 参数
//	 */
//	public static void sendMessage1(String signName, String templateCode,String phoneNumbers,String param){
//		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
//		IAcsClient client = new DefaultAcsClient(profile);
//
//		SendSmsRequest request = new SendSmsRequest();
//		request.setSysRegionId("cn-hangzhou");
//		request.setPhoneNumbers(phoneNumbers);
//		request.setSignName(signName);
//		request.setTemplateCode(templateCode);
//		request.setTemplateParam("{\"code\":\""+param+"\"}");
//		try {
//			SendSmsResponse response = client.getAcsResponse(request);
//			System.out.println("短信发送成功");
//		}catch (ClientException e) {
//			e.printStackTrace();
//		}
//	}

    //没缴费，但功能已经实现
//	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param) {
//        SendSmsResponse sendSmsResponse = null;
//
//
//            //初始化acsClient,暂不支持region化
//            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5t7LNtZTUT76RhyJASbE", "7fsmV3XWo0D1IeyOkGLnIXZ7fK33In");
//
//            IAcsClient acsClient = new DefaultAcsClient(profile);
//
//            //4.组装请求对象-具体描述见控制台-文档部分内容
//            SendSmsRequest request = new SendSmsRequest();
//            //必填:待发送手机号
//            request.setPhoneNumbers(phoneNumbers);
//            //必填:短信签名-可在短信控制台中找到
//            request.setSignName(signName);
//            //必填:短信模板-可在短信控制台中找到
//            request.setTemplateCode(templateCode);
//            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//            request.setTemplateParam("{\"code\":\""+param+"\"}");
//
//            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
//            //request.setSmsUpExtendCode("90997");
//
//            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//            //request.setOutId("sgaet2020168");
//
//
//            try {
//                SendSmsResponse response = acsClient.getAcsResponse(request);
//                System.out.println("短信发送成功");
//            } catch (ClientException e) {
//                e.printStackTrace();
//            }
//
//
//        }


    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "";
    static final String accessKeySecret ="";
    public static SendSmsResponse sendSms(String signName,String phone , String TemplateCode,String TemplateP) throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号，这里都是使用传输的参数的，没有写死。
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);//需要修改为自己的签名
        //必填:短信模板-可在短信控制台中找到
        //request.setTemplateCode("SMS_152440521");//两种方法，可以直接指定模板
        request.setTemplateCode(TemplateCode);//获取调用方法，根据传的参数指定模板。二者任选其一。

        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(TemplateP);

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            return sendSmsResponse;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }







    }
