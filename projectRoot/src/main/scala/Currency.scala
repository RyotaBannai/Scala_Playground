abstract class CurrencyZone {

  /** @prop Currency はサブクラスで実際の通貨単位を表現 */
  type Currency <: AbstractCurrency
  def make(amount: Long): Currency

  abstract class AbstractCurrency {
    val amount: Long
    private def decimals(n: Long): Int = if (n == 1) 0 else 1 + decimals(n / 10)
    override def toString(): String =
      ((amount.toDouble / CurrencyUnit.amount.toDouble) formatted ("%." + decimals(
        CurrencyUnit.amount
      ) + "f") + " " + designation)

    /** @prop designation 通貨単位 @example USD, Yen, Euro */
    def designation: String
    def +(that: Currency): Currency = make(this.amount + that.amount)
    def *(x: Double): Currency = make((this.amount * x).toLong)
  }

  val CurrencyUnit: AbstractCurrency
}

abstract class US extends CurrencyZone {
  abstract class Dollar extends AbstractCurrency {
    def designation: String = "USD"
  }
  type Currency = Dollar
  override def make(cents: Long): Dollar = new Dollar { val amount = cents }
  val Cent = make(1)
  val Dollar = make(100)
  override val CurrencyUnit = Dollar
}

abstract class Europe extends CurrencyZone {
  abstract class Euro extends AbstractCurrency {
    def designation: String = "EUR"
  }
  type Currency = Euro
  override def make(cents: Long): Euro = new Euro { val amount = cents }
  val Cent = make(1)
  val Euro = make(100)
  override val CurrencyUnit = Euro
}

abstract class Japan extends CurrencyZone {
  abstract class Yen extends AbstractCurrency {
    def designation: String = "JPY"
  }
  type Currency = Yen
  override def make(yen: Long): Yen = new Yen { val amount = yen }
  val Yen = make(1)
  override val CurrencyUnit = Yen
}

/*
val jp = new Japan{}
jp.make(1000)
 */
