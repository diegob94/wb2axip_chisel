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
    val SLAVE_ADDR: Vector[Boolean],
    val SLAVE_MASK: Vector[Boolean],
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
    /** Use this function to instance the module. */
    def apply(
      NM: Int = 4,
      NS: Int = 8,
      AW: Int = 32,
      DW: Int = 32,
      SLAVE_ADDR: Option[Vector[Boolean]] = None,
      SLAVE_MASK: Option[Vector[Boolean]] = None,
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
      received_slave_addr: Option[Vector[Boolean]]
    ) : Vector[Boolean] = {
      received_slave_addr match {
        case Some(value) => value
        case None => {
          require(NS == 8 && AW >= 4)
          Vector(
            Vector(true, true, true),  Vector.fill(AW-3)(false),
            Vector(true, true, false), Vector.fill(AW-3)(false),
            Vector(true, false, true), Vector.fill(AW-3)(false),
            Vector(true, false, false), Vector.fill(AW-3)(false),
            Vector(false, true, true), Vector.fill(AW-3)(false),
            Vector(false, false, true, false), Vector.fill(AW-4)(false),
            Vector(false, false, false, false), Vector.fill(AW-4)(false),
          ).flatten
          /*
          Cat(
            "b111".U, Fill(AW-3, "b0".U),
            "b110".U, Fill(AW-3, "b0".U),
            "b101".U, Fill(AW-3, "b0".U),
            "b100".U, Fill(AW-3, "b0".U),
            "b011".U, Fill(AW-3, "b0".U),
            "b0010".U, Fill(AW-4, "b0".U),
            "b0000".U, Fill(AW-4, "b0".U),
          )
          */
        }
      }
    }

    def SLAVE_MASK_default (
      NS: Int = 8,
      AW: Int = 32,
      received_slave_mask: Option[Vector[Boolean]]
    ) : Vector[Boolean] = {
      received_slave_mask match {
        case Some(value) => value
        case None => {
          require(AW >= 4)
          if (NS <= 1)
            Vector.fill(NS*AW)(false)
            // Fill(NS*AW, "b0".U)
          else
            (
              Vector.fill(NS-2)(Vector.fill(3)(false) ++ Vector.fill(AW-3)(false)) ++
              Vector.fill(2)(Vector.fill(4)(false) ++ Vector.fill(AW-4)(false))
            ).flatten
          /*
          Cat(
            Fill(NS-2, Cat("b111".U, Fill(AW-3, "b0".U))),
            Fill(2, Cat("b1111".U, Fill(AW-4, "b0".U))),
          )
          */
        }
      }
    }

    def format_bits(b: Vector[Boolean]): String = {
      return "%s'b%s".format(
        b.length,
        b.map(bb => if (bb) "1" else "0").mkString("")
      )
    }

  }


}