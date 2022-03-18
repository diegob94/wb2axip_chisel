import java.io._

package org.study.wb2axip {

  package object resources {

    final val resourcesWb2axipRtlPathEnvVar: String = "WB2AXIP_CHISEL_RLT_PATH"

    def get_wb2axip_rtl_path() : String = {
      return "wb2axip/rtl/"
    }

  }

}
