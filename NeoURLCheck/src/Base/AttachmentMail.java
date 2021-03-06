/**
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// [START simple_includes]
import java.util.Date;
import java.util.Properties;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
// [END simple_includes]

// [START multipart_includes]
import java.io.UnsupportedEncodingException;
// [END multipart_includes]


@SuppressWarnings("serial")
public class AttachmentMail {

    public void sendEmail(String host, String port,
                              final String userName, final String password, String[] pathFileInfo, String toAddress,
                              String subject, String message) throws AddressException,
            MessagingException {

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.tls.trust","mail.gmail.com"); // Could not convert socket to TLS; Server is not trusted
        properties.put("mail.debug", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };


        Session session = Session.getInstance(properties, auth);

        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(userName);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};

        Address ccAddr = new InternetAddress("neo.jd@neolab.net");
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.addRecipient(Message.RecipientType.CC, ccAddr);
        msg.setSubject(subject);
        msg.setSentDate(new Date());


        // ?????? ????????? ??????
        Multipart mParts = new MimeMultipart();
        MimeBodyPart mTextPart = new MimeBodyPart();

        // ?????? ????????? - ??????
        /**********************************************
         * ?????? ??????
         **********************************************/
            mTextPart.setText(message, "UTF-8", "html");
            mParts.addBodyPart(mTextPart);


        /**********************************************
         * ?????? ??????. ??????????????? ??????
         **********************************************/
        int cnt = pathFileInfo.length;

            for(int i=0; i < cnt ;i++) {

            MimeBodyPart attachPart = new MimeBodyPart();

            DataSource source = new FileDataSource(pathFileInfo[i]);
            attachPart.setDataHandler(new DataHandler(source));

            //??????????????? ????????? ????????? ????????? ??????
            try {

                attachPart.setFileName(MimeUtility.encodeText(source.getName(), "euc-kr","B"));
                mParts.addBodyPart(attachPart);

            } catch (UnsupportedEncodingException e) {
                System.out.println("?????? endcode ?????? ??????");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }


        }

        /**********************************************
         * mParts ??????, ?????? ?????? ?????????  ~~~~
         **********************************************/
            msg.setContent(mParts);

        // MIME ?????? ??????
        MailcapCommandMap MailcapCmdMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
             MailcapCmdMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
             MailcapCmdMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
             MailcapCmdMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
             MailcapCmdMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
             MailcapCmdMap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
             CommandMap.setDefaultCommandMap(MailcapCmdMap);

        // ?????? ??????
             Transport.send(msg);
        //System.out.println("????????? ?????? ????????? : "+sysdate);
    }

    public static void main(String[] args) {
        // SMTP server information
        //String host = "smtp.naver.com";
        String host = "smtp.gmail.com"; // gmail ?????? ??????
        String port = "587"; // gmail ?????? ??????
        String mailFrom = "neolab.tester@gmail.com"; // gmail ?????? ??????
        String password = "kyqrxebyukvqgdal"; //

        // outgoing message information
        String mailTo = "_qa@neolab.net"; //?????????
        String subject = "???????????????"; // ??????

        // message contains HTML markups ??????
        String message = "<i> send email! </i><br>";
        message += "<b> ???????????????! </b><br>";
        message += "<font color=red>finish</font>";

        String[] attachment = new String[1]; // ????????? ?????? ?????? ?????? String ?????? ??????
        attachment[0] = "/Users/jd/IdeaProjects/NeoStudio/test result/NeoStudio_TestCase.xls"; // ????????? ?????? ??????


        AttachmentMail mailer = new AttachmentMail();

        try {
            mailer.sendEmail(host, port, mailFrom, password, attachment, mailTo,
                    subject, message);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Failed to sent email.");
            ex.printStackTrace();
        }
    }
}