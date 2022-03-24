
import chisel3._
import chisel3.util._
import chisel3.experimental.VecLiterals._
import org.study.wb2axip._
import wishbone._

import org.scalatest.funsuite.AnyFunSuite

package org.study.wb2axip.test {
  class WbxbarTests extends AnyFunSuite {
    test("can instantiate wbxbar IP") {
      class WbxbarIpWrapper extends Module {
        val internal = Module(new WbxbarIp(2,2,4,8,Vec.Lit("b0001".U(4.W),"b0010".U(4.W)),Vec.Lit("b0011".U(4.W),"b0011".U(4.W))))
      }
      (new chisel3.stage.ChiselStage).emitVerilog(new WbxbarIpWrapper(), Array("--target-dir", "work"))
    }
    test("can generate wbxbar verilog") {
      class WbTestModule extends Module {
        val addr_width = 8
        val data_width = 8
        val source1 = IO(new WishboneSink(addr_width=addr_width,data_width=data_width))
        val source2 = IO(new WishboneSink(addr_width=addr_width,data_width=data_width))
        val sink1 = IO(new WishboneSource(addr_width=addr_width,data_width=data_width))
        val sink2 = IO(new WishboneSource(addr_width=addr_width,data_width=data_width))
        val xbar = Module(new Wbxbar(
          addr_width=addr_width,
          data_width=data_width,
          source_count=2,
          sink_count=2,
          sink_address=Vec.Lit("b0001".U,"b0010".U),
          sink_mask=Vec.Lit("b0011".U,"b0011".U)
        ))
        xbar.sources(0) <> source1
        xbar.sources(1) <> source2
        xbar.sinks(0) <> sink1
        xbar.sinks(1) <> sink2
      }
      (new chisel3.stage.ChiselStage).emitVerilog(new WbTestModule, Array("--target-dir", "work"))
    }
  }
}
