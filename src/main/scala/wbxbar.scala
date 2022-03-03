import org.study.wb2axip.resources

import chisel3._
import chisel3.experimental.IntParam
import chisel3.util.{HasBlackBoxResource, Cat, Fill}

package org.study.wb2axip {

  class Wbxbar(
    val NM: Int,
    val NS: Int,
    val AW: Int,
    val DW: Int,
    val SLAVE_ADDR: Bits,
    val SLAVE_MASK: Bits,
  ) extends BlackBox(
    Map(
      "NM" -> IntParam(NM),
      "NS" -> IntParam(NS),
      "AW" -> IntParam(AW),
      "DW" -> IntParam(DW),
      "SLAVE_ADDR" -> IntParam(SLAVE_ADDR.litValue),
      "SLAVE_MASK" -> IntParam(SLAVE_MASK.litValue),
    )
  ) with HasBlackBoxResource {
    val io = IO(new Bundle {
      val i_clk    = Input(Clock())
      val i_reset  = Input(Bool())
      
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
    })
    addResource(s"${resources.get_wb2axip_rtl_path()}/wbxbar.v")
  }

  object Wbxbar {
    def apply(
      NM: Int = 4,
      NS: Int = 8,
      AW: Int = 32,
      DW: Int = 32,
      SLAVE_ADDR: Option[Bits] = None,
      SLAVE_MASK: Option[Bits] = None,
    ) : Wbxbar = {
      new Wbxbar(
        NM, NS, AW, DW,
        this.SLAVE_ADDR_default(NS, AW, SLAVE_ADDR),
        this.SLAVE_MASK_default(NS, AW, SLAVE_MASK),
      )
    }

    def SLAVE_ADDR_default (
      NS: Int = 8,
      AW: Int = 32,
      received_slave_addr: Option[Bits]
    ) : Bits = {
      received_slave_addr match {
        case Some(value) => value
        case None => {
          require(NS == 8 && AW >= 4)
          Cat(
            "b111".U, Fill(AW-3, "b0".U),
            "b110".U, Fill(AW-3, "b0".U),
            "b101".U, Fill(AW-3, "b0".U),
            "b100".U, Fill(AW-3, "b0".U),
            "b011".U, Fill(AW-3, "b0".U),
            "b0010".U, Fill(AW-4, "b0".U),
            "b0000".U, Fill(AW-4, "b0".U),
          )
        }
      }
    }

    def SLAVE_MASK_default (
      NS: Int = 8,
      AW: Int = 32,
      received_slave_mask: Option[Bits]
    ) : Bits = {
      received_slave_mask match {
        case Some(value) => value
        case None => {
          require(AW >= 4)
          if (NS <= 1)
            Fill(NS*AW, "b0".U)
          else
          Cat(
            Fill(NS-2, Cat("b111".U, Fill(AW-3, "b0".U))),
            Fill(2, Cat("b1111".U, Fill(AW-4, "b0".U))),
          )
        }
      }
    }
  }


}

/*
parameter	NM = 4, NS=8,
parameter	AW = 32, DW=32,
parameter	[NS*AW-1:0]	SLAVE_ADDR = {
    { 3'b111, {(AW-3){1'b0}} },
    { 3'b110, {(AW-3){1'b0}} },
    { 3'b101, {(AW-3){1'b0}} },
    { 3'b100, {(AW-3){1'b0}} },
    { 3'b011, {(AW-3){1'b0}} },
    { 3'b010, {(AW-3){1'b0}} },
    { 4'b0010, {(AW-4){1'b0}} },
    { 4'b0000, {(AW-4){1'b0}} } },
parameter	[NS*AW-1:0]	SLAVE_MASK = (NS <= 1) ? 0
  : { {(NS-2){ 3'b111, {(AW-3){1'b0}} }},
    {(2){ 4'b1111, {(AW-4){1'b0}} }} },
*/