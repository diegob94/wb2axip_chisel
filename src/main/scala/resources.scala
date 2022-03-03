import java.io._

package org.study.wb2axip {

  package object resources {

    final val resourcesWb2axipRtlPathEnvVar: String = "WB2AXIP_CHISEL_RLT_PATH"

    def get_wb2axip_rtl_path() : String = {
      val thisFileLocation: File =  new File(".").getCanonicalFile.getAbsoluteFile
      val repoRoot: File = thisFileLocation.getParentFile.getParentFile.getParentFile.getParentFile
      return repoRoot.toPath.resolve("submodules").resolve("wb2axip").resolve("rtl").toAbsolutePath.toString
    }

  }

}
