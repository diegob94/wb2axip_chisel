
import chisel3._
import chisel3.util._
import chisel3.experimental.VecLiterals._
import org.study.wb2axip.Wbxbar

import org.scalatest.funsuite.AnyFunSuite

package org.study.wb2axip.test {

  class WbxbarWrapper extends Module {
    val internal = Module(new Wbxbar(2,2,4,8,Vec.Lit("b0001".U(4.W),"b0010".U(4.W)),Vec.Lit("b0011".U(4.W),"b0011".U(4.W))))
  }

  class WbxbarTests extends AnyFunSuite {
    test("can generate wbxbar verilog") {
      (new chisel3.stage.ChiselStage).emitVerilog(new WbxbarWrapper(), Array("--target-dir", "work"))
    }

  }
}
