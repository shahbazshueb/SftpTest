//https://kodehelp.com/sftp-connection-public-key-authentication-java/
//http://blog.jscape.com/jscape/2008/08/uploading-files.html
//https://kodehelp.com/java-program-for-downloading-file-from-sftp-server/
import java.io._

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException
import util.control.Breaks._


object SftpTest extends App {
  override def main(args: Array[String]): Unit = {
    val jsch = new JSch
    try {
      val session = jsch.getSession("shahbaz", "172.16.0.68", 22)
      session.setPassword("tenpearls")
      session.setConfig("StrictHostKeyChecking", "no")
      session.connect()
      val channel = session.openChannel("sftp");
      channel.connect
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      channelSftp.cd("/home/shahbaz/sharefiles")
      val bis = new BufferedInputStream(channelSftp.get("download"))
      val newFile = new File("testDownload")
      val os: OutputStream = new FileOutputStream(newFile)
      val bos = new BufferedOutputStream(os)
      val buffer = new Array[Byte](1024)
      var readCount: Int = 0;
      breakable{
        while(true) {
          readCount = bis.read(buffer)
          println(readCount)
          if (readCount < 0)
            break
          bos.write(buffer, 0, readCount)
        }
      }
      bis.close()
      bos.close()
      channelSftp.disconnect()
      session.disconnect()
    } catch  {
      case e => e.printStackTrace()
    }
  }
}
