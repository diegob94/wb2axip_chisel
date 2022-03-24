import org.study.wb2axip.resources

import chisel3._
import chisel3.experimental.{IntParam, RawParam}
import chisel3.util.{HasBlackBoxResource, Cat, Fill}
import chisel3.experimental.VecLiterals._
import chisel3.util.experimental.{InlineInstance,forceName}

import wishbone._

import scala.collection.mutable.Buffer

package org.study.wb2axip {
  class WbxbarIp(
    val NM: Int,
    val NS: Int,
    val AW: Int,
    val DW: Int,
    val SLAVE_ADDR: Vec[UInt],
    val SLAVE_MASK: Vec[UInt],
  ) extends BlackBox(
    Map(
      "NM" -> IntParam(NM),
      "NS" -> IntParam(NS),
      "AW" -> IntParam(AW),
      "DW" -> IntParam(DW),
      "SLAVE_ADDR" -> RawParam(WbxbarIp.format_bits(SLAVE_ADDR)),
      "SLAVE_MASK" -> RawParam(WbxbarIp.format_bits(SLAVE_MASK)),
    )
  ) with HasBlackBoxResource {
    val io = IO(
      new Bundle {
        val i_clk    = Input(Clock())
        val i_reset  = Input(Reset())
        
        val i_mcyc   = Input(UInt(NM.W))
        val i_mstb   = Input(UInt(NM.W))
        val i_mwe    = Input(UInt(NM.W))

        val i_maddr  = Input(UInt((NM*AW).W))
        val i_mdata  = Input(UInt((NM*DW).W))
        val i_msel   = Input(UInt((NM*DW/8).W))

        val o_mstall = Output(UInt(NM.W))
        val o_mack   = Output(UInt(NM.W))
        val o_mdata  = Output(UInt((NM*DW).W))
        val o_merr   = Output(UInt(NM.W))

        val o_scyc   = Output(UInt(NS.W))
        val o_sstb   = Output(UInt(NS.W))
        val o_swe    = Output(UInt(NS.W))

        val o_saddr  = Output(UInt((NS*AW).W))
        val o_sdata  = Output(UInt((NS*DW).W))
        val o_ssel   = Output(UInt((NS*DW/8).W))

        val i_sstall = Input(UInt(NS.W))
        val i_sack   = Input(UInt(NS.W))
        val i_sdata  = Input(UInt((NS*DW).W))
        val i_serr   = Input(UInt(NS.W))
      }
    )
    override def desiredName = "wbxbar"
    addResource(s"${resources.get_wb2axip_rtl_path()}/wbxbar.v")
  }
  object WbxbarIp {
    def format_bits(b: Vec[UInt]): String = {
      return "%s'h%s".format(
        b.getWidth,
        b.litValue.toString(16)
      )
    }
  }
  class Wbxbar(addr_width: Int, data_width: Int, source_count: Int, sink_count: Int, sink_address: Vec[UInt], sink_mask: Vec[UInt]) extends Module with InlineInstance {
    val sources = IO(Vec(source_count,new WishboneSink(addr_width=addr_width,data_width=data_width)))
    val sinks = IO(Vec(sink_count,new WishboneSource(addr_width=addr_width,data_width=data_width)))
    val xbar_ip = Module(new WbxbarIp(
      NM=source_count,
      NS=sink_count,
      AW=addr_width,
      DW=data_width,
      SLAVE_ADDR=sink_address,
      SLAVE_MASK=sink_mask
    ))
    forceName(xbar_ip,"xbar_ip")
    xbar_ip.io.i_maddr := Cat(sources.map(s => s.adr))
    xbar_ip.io.i_mdata := Cat(sources.map(s => s.datwr))
    xbar_ip.io.i_sdata := Cat(sinks.map(s => s.datrd))
    xbar_ip.io.i_mwe := Cat(sources.map(s => s.we))
    xbar_ip.io.i_mcyc := Cat(sources.map(s => s.cyc))
    xbar_ip.io.i_mstb := Cat(sources.map(s => s.stb))
    xbar_ip.io.i_sack := Cat(sinks.map(s => s.ack))
    xbar_ip.io.i_msel := Cat(sources.map(s => s.sel))
    sinks.zipWithIndex.map { case (s, i) =>
      s.adr := xbar_ip.io.o_saddr(i+addr_width,i)
      s.datwr := xbar_ip.io.o_sdata(i+data_width,i)
      s.we := xbar_ip.io.o_swe(i)
      s.cyc := xbar_ip.io.o_scyc(i)
      s.stb := xbar_ip.io.o_sstb(i)
      s.sel := xbar_ip.io.o_ssel(i)
    }
    sources.zipWithIndex.map { case (s, i) =>
      s.datrd := xbar_ip.io.o_mdata(i+data_width,i)
      s.ack := xbar_ip.io.o_mack(i)
    }
  }
}
