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
    Try {
      jsch.addIdentity(
        """
          |MIIBuwIBAAKBgQCN82wqHg8NoDpZxCUWjMajrBPxgPRjgB68LTUIJMet9gC5gggm
          |bbXVmGU1lljijlHVCFM7xCI1f+NvWJUX0wQYKvRavCAy7T4kDQdf6OWkjQeeWVQi
          |t2v2NdIs2TzkkeYtGgxC0VtyAoMRt9C+L78HIJ3zi0Gk+ROePLrnXuSGMwIVAOhQ
          |gztcFlRzTJbKuCRyCpHPbEQHAoGATL73weqvjaNASGjr/BcXQHP4DzFxByxDH5VH
          |j8IFSp53R/aqf9344R0mE25EymfhYrqeO3mQdyDRlW8hwtOhtsY73ZMIWm66M+tD
          |IBibbl8G8w2AtoiQdOd2qv0ZsScuAmEplfWYORDVlaks6qjzIs0PW7HiBe1as+Ct
          |wxmDLhYCgYBk7BUEp4U7MDolfmps3J/VNAj2YkwbV/9eFWIguFabXlV+79/XU6tT
          |IwX3NCryvnrAuKWygkz0ocC7f2yZTu7rOFSN38XFsD41gflFDQrHLWWa/xBlbkMK
          |JqPeNWiUmS23Lbk1NDhxaKPd5quad+WAP6I1Phecs2FtkcRerxN8wgIVAKAQFl99
          |BWfVJrYO919USyX15Kwh
        """.stripMargin)
      val session = jsch.getSession("tenpearls", "172.16.0.68", 22)
      session.setPassword("tenpearls")
      session.setConfig("StrictHostKeyChecking", "no")
      session.connect()
      val channel = session.openChannel("sftp");
      channel.connect
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      channelSftp.cd("/home/shahbaz/sharefiles")
      val bis = new BufferedInputStream(channelSftp.get("download.csv"))
      val newFile = new File("testDownload.csv")
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
      newFile
    } match {
      case Success(file) =>
        val reader = CSVReader.open(file)
        val list = reader.allWithHeaders();
        list.head += ("first_name" -> "shahbaz")
        println(list.head("first_name"))
    }

  }
  def getSftpChannel(): Unit = {

  }
}
