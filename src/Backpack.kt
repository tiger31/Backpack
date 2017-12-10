import java.util.*

class Backpack(val weight: Int, val itemsCost: Array<Int>, val itemsWeight: Array<Int>) {
    val summaryItemsValue: Int = itemsCost.sum()
    val random = Random()
    var mutations = 1000
    var generation = createGeneration()

    fun solve(): Array<Boolean> {
        var fittest = fittestUnit(generation)
        var fittestSurvCoeff = surviveCoeff(fittest)
        var generationSurvCoeff = generationSurviveCoeff(generation)

        while (fittestSurvCoeff != 1.0 && mutations > 0) {
            //Создаем новое поколение
            generation = xcrossGeneration(generation)

            //Если коэффициент выживаемости нового поколения ниже чем у предыдущего - подключаем мутацию
            if (generationSurviveCoeff(generation) <= generationSurvCoeff) {
                val index = random.nextInt(itemsCost.size - 1)
                generation[index] = mutateUnit(generation[index])
                mutations--
            }
            //Если все еще ниже (невероятно!), заменяем случайную особь нового поколения на другую случайно созданную
            //Заметил, что без этого алгорим "упирался" в "стену"
            //Алгорим мог приблизиться к решению, но не найти, т.к., чтобы найти решение, приходилось бы "играть на понижение" индекса выживаемости
            //Со "вбросом" случайной особи все работает довольно точно даже на 1000 мутаци (а может и на 100)(было 10000)
            if (generationSurviveCoeff(generation) <= generationSurvCoeff) {
                val index = random.nextInt(itemsCost.size - 1)
                generation[index] = createUnit()
            }

            val nextGenFittest = fittestUnit(generation)
            fittest = if (surviveCoeff(nextGenFittest) > fittestSurvCoeff) nextGenFittest else fittest
            fittestSurvCoeff = surviveCoeff(fittest)
            generationSurvCoeff = generationSurviveCoeff(generation)
        }

        fittest.forEach { print(if (it) 1 else 0) }
        println(" /  $${fittest.mapIndexed{i, b -> if (b) itemsCost[i] else 0 }.sum()}, ${fittest.mapIndexed { index, b -> if (b) this.itemsWeight[index] else 0 }.sum()}")
        return fittest
    }

    //Расчеты
    fun countSummaryWeight(unit: Array<Boolean>) : Int = unit.mapIndexed { index, b -> if (b) this.itemsWeight[index] else 0 }.sum()
    fun countSummaryCost(unit: Array<Boolean>): Int = unit.mapIndexed { index, b -> if (b) this.itemsCost[index] else 0 }.sum()

    //Эстиматические оценки выживаемости
    fun surviveCoeff(unit: Array<Boolean>): Double = if (countSummaryWeight(unit) > this.weight) 0.0 else 1 * (countSummaryCost(unit).toDouble() / summaryItemsValue)
    fun generationSurviveCoeff(generation: Array<Array<Boolean>>): Double = generation.sumByDouble { surviveCoeff(it) } / itemsCost.size

    //Создание/изменение особей
    fun createUnit() : Array<Boolean> = Array(itemsCost.size, { _ -> val r = Random(); r.nextBoolean() })
    fun mutateUnit(unit: Array<Boolean>, index: Int = random.nextInt(unit.size - 1)) : Array<Boolean> = unit.clone()
            .mapIndexed { i, b -> if (i == index) { !b }else b }
            .toTypedArray()
    //Thiiis! Isss! Survival of the fittest!
    fun fittestUnit(generation: Array<Array<Boolean>>) : Array<Boolean> = generation.maxBy { surviveCoeff(it) }!!
    fun crossUnits(unit1: Array<Boolean>, unit2: Array<Boolean>, shift: Int): Array<Boolean> = arrayOf(
            *unit1.take(shift).toTypedArray(),
            *unit2.takeLast(itemsCost.size - shift).toTypedArray()
    )

    //Создание/изменение поколений
    fun createGeneration() : Array<Array<Boolean>> = Array(itemsCost.size, {}).map { createUnit() }.toTypedArray()
    fun xcrossGeneration(generation: Array<Array<Boolean>>): Array<Array<Boolean>> {
        val survivors = generation.sortedBy { surviveCoeff(it) }.take(deFactorial(generation.size))
        val survivorsCount = survivors.size - 1 //Почти количество выживших, правда на одного меньше :[
        val generationTemplate = createGeneration().map {
            crossUnits(survivors[random.nextInt(survivorsCount)], survivors[random.nextInt(survivorsCount)], 1 + random.nextInt(generation.size - 2))
        }.toTypedArray()
        return generationTemplate
    }


    //Чтобы сформировать поколение более пригодное для выживания нужно оставить только самых устойчивых сообей
    //Если учесть то, что особь не может размножаться сама с собой, то нужное их кол-во будет числом под факториал общего ко-ва особой в фаториале
    private fun deFactorial(factorial: Int): Int {
        var prev = 0
        var value = 1
        while (value < factorial) {
            prev++
            value *= prev
        }
        return prev
    }
}
