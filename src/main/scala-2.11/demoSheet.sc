/*
 * - Car class
 * - DSL drive
 * - Singleton
 * - List
 * - .Foreach / for by
 * - Trait machine
 */
class Car(val year:Int, var km:Int) {
  def drive(km:Int): Unit = {
    this.km += km
  }
}



for(i <- 1 to 10 ) println(i)


val list = List(1,2,3,4,5,6,7)


list.foreach{ println }













object bmw {

}

val car = new Car(2015, 10)
car.km
car.drive(10)


car.km



val test = "Hello guys";
