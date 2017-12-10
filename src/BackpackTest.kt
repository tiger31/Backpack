import org.junit.Test
import org.junit.Assert.*
class BackpackTest {
    @Test
    fun costOverWeight() {
        var backpack = Backpack(15, arrayOf(5, 12, 6), arrayOf(9, 12, 6))
        assertArrayEquals(arrayOf(false, true, false), backpack.solve())
        //Убеждаемся, что алгорится не возьмет предмет сверх емкости
        backpack = Backpack(15, arrayOf(5, 12, 6, 100), arrayOf(9, 12, 6, 20))
        assertArrayEquals(arrayOf(false, true, false, false), backpack.solve())
    }
    @Test
    fun ifNotEnoughWeight() {
        val backpack = Backpack(15, arrayOf(1, 1, 1, 1), arrayOf(1, 1, 1, 1))
        assertArrayEquals(arrayOf(true, true, true, true), backpack.solve())
    }
    @Test
    fun common() {
        var backpack = Backpack(30, arrayOf(17, 1, 4, 9, 10, 5), arrayOf(10, 3, 1, 3, 20, 7))
        assertArrayEquals(arrayOf(true, true, true, true, false, true), backpack.solve())
        backpack = Backpack(14, arrayOf(2, 5, 4, 3), arrayOf(5, 10, 6, 5))
        assertArrayEquals(arrayOf(false, false, true, true), backpack.solve())
        //Небольшое разнообразие тестов, т.к. найти готовые варианты заданий в интернете довольно тяжело,
        //А высчитывать вручную большие количества предметов довольно тяжело.
        //При желании можете запустить тест на своем алгоритме :3
    }
}
