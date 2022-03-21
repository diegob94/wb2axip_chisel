
import chisel3._
import chisel3.util._
import org.study.wb2axip.Wbxbar

import org.scalatest.funsuite.AnyFunSuite

package org.study.wb2axip.test {

  class WbxbarWrapper extends Module {
    val internal = Module(Wbxbar())
  }

  class WbxbarTests extends AnyFunSuite {
    test("can generate wbxbar verilog") {
      (new chisel3.stage.ChiselStage).emitVerilog(new WbxbarWrapper(), Array("--target-dir", "work"))
    }

  }
}
