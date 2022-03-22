import org.study.wb2axip.resources

import chisel3._
import chisel3.experimental.{IntParam, RawParam}
import chisel3.util.{HasBlackBoxResource, Cat, Fill}

package org.study.wb2axip {

  /** Do not instance this module directly, see object Wbxbar */
  class Wbxbar(
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
      "SLAVE_ADDR" -> RawParam(Wbxbar.format_bits(SLAVE_ADDR)),
      "SLAVE_MASK" -> RawParam(Wbxbar.format_bits(SLAVE_MASK)),
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
    addResource(s"${resources.get_wb2axip_rtl_path()}/wbxbar.v")
  }

  object Wbxbar {
    def format_bits(b: Vec[UInt]): String = {
      return "%s'h%s".format(
        b.getWidth,
        b.litValue.toString(16)
      )
    }
  }
}
