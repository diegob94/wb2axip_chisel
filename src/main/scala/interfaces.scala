package wishbone

import chisel3._

class WishboneSource(addr_width: Int, data_width: Int) extends Bundle {
  val sel_width = data_width / 8
  val adr = Output(UInt(addr_width.W))
  val datwr = Output(UInt(data_width.W))
  val datrd = Input(UInt(data_width.W))
  val we = Output(Bool())
  val cyc = Output(Bool())
  val stb = Output(Bool())
  val ack = Input(Bool())
  val sel = Output(UInt(sel_width.W))
}

class WishboneSink(addr_width: Int, data_width: Int) extends Bundle {
  val sel_width = data_width / 8
  val adr = Input(UInt(addr_width.W))
  val datwr = Input(UInt(data_width.W))
  val datrd = Output(UInt(data_width.W))
  val we = Input(Bool())
  val cyc = Input(Bool())
  val stb = Input(Bool())
  val ack = Output(Bool())
  val sel = Input(UInt(sel_width.W))
}

