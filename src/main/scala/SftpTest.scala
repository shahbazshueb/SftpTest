//https://kodehelp.com/sftp-connection-public-key-authentication-java/
//http://blog.jscape.com/jscape/2008/08/uploading-files.html
//https://kodehelp.com/java-program-for-downloading-file-from-sftp-server/
///https://github.com/tototoshi/scala-csv
//https://github.com/melrief/PureCSV
import java.io._

import com.github.tototoshi.csv.CSVReader
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpException

import scala.util.{Success, Try}
import util.control.Breaks._


object SftpTest extends App {
  override def main(args: Array[String]): Unit = {
    val jsch = new JSch
    val privateKey =
      """-----BEGIN RSA PRIVATE KEY-----
        |MIICXAIBAAKBgQDRqrZ7eDX1fn9G2IKG/YMPDsEtfAKL6VZvb9eEtYqzZ6nVpDkI
        |t5qNy1j3aKV2HH+TWbOLAxKw+4XM8niT2DFPZ3Wwle14qyL8S0JKgm3GsotAR9kk
        |RBNAVBsjkScZoAP5PiZmPWF4YKi8kyxzjwH/5nO0ILARuZEthgKUD/ALOwIDAQAB
        |AoGANcW6l1/1NskCi4DruQM7oZj/IlMzs/5cFKhyda65q9liTVCY8AtmsAb/AjTs
        |eqZEcd4Hlhdrq8hWQIHPOtuviV8BAjFYN8nEY7p9P9zk6wOH6n9mBAp31d6gf908
        |fFdx038fs41LhkkWvQfSZieDhGJTHFpo27Kh+8R7aCNFjwECQQDod3+3INkl42T5
        |7on3nBEx7UKXPsTaRSorjy7yoFaTi79WLZZMMxcptOOptCN5OjE5STJ8HlYWWJt4
        |FrpVaTxxAkEA5uRXVom2Ro44aojXarNe3M99o63wCbsSxSUEnZtzqqeUbH4SZPMb
        |keVBHOXiRaQL/4rJFTK4qPur6MVGzTRIawJBAOOTSOZgM/TRfiLnEQ3kLTkxSkWC
        |X3hGyZfHHnDL4rWi03dsPXzvbzeXLGEQRsIA4/tu2wuDL0fFCr4vkc7XrSECQGnc
        |h67FtSzkSDmQRDRHyVa0S4agWU4c155MOrGIdJ/p9cNIimZ+kEV0N0ZooDYN4PJp
        |T4jImKHhwhAXYz7ymcECQDvfrtotqjnzSUFRh8vXarusbFXkpPn5Z7MqDgyQ5spP
        |okoj98L6xM9nYaafSwchBi3glcDL69dcdn2Q+c6dfXU=
        |-----END RSA PRIVATE KEY-----""".stripMargin.getBytes
    val emptyPassPhrase = "tenpearls".getBytes
    Try {
      jsch.addIdentity("tenpearls", privateKey, null, emptyPassPhrase)
      val session = jsch.getSession("tenpearls", "172.16.0.68", 22)
      session.setPassword("tenpearls")
      session.setConfig("StrictHostKeyChecking", "no")
      session.connect()
      val channel = session.openChannel("sftp");
      channel.connect
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      //channelSftp.cd("/home/shahbaz/sharefiles")
      val bis = new BufferedInputStream(channelSftp.get("download.csv"))
      val newFile = new File("testDownload.csv")
      val os: OutputStream = new FileOutputStream(newFile)
      val bos = new BufferedOutputStream(os)
      val buffer = new Array[Byte](1024)
      var readCount: Int = 0;
      breakable {
        while (true) {
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
      newFile
    } match {
      case Success(file) =>
        val reader = CSVReader.open(file)
        val list = reader.allWithHeaders();
        //list.head += ("first_name" -> "shahbaz")
        println(list.head("first_name"))
    }

  }

  def getSftpChannel(): Unit = {

  }
}
