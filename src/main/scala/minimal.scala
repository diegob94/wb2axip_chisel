
import chisel3._
import chisel3.util._
import org.study.wb2axip.Wbxbar

package org.study.wb2axip.test {

  class Top extends Module {
    val internal = Module(Wbxbar())
  }

  object WbxbarInstance extends App {
    (new chisel3.stage.ChiselStage).emitVerilog(new Top(), args)
  }
}