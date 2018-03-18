// Last change: 2018-03-18

package com.sebastianbechtold.vectro

var em = ManfredEcs()

abstract class ManfredComponent {
    var id: Long = 0
}

class ManfredEcs {

    // NOTE: entities must be public since 'getComponent()' must be 'inline fun <reified T>'.
    var entities = HashMap<Long, HashMap<Any, ManfredComponent>>()

    private var _deleteList = HashSet<Long>()

    private var _nextId: Long = 0

    fun getUnusedId(): Long {
        return _nextId++
    }


    inline fun <reified T> getComponent(id: Long, compClass: Class<T>): T? {

        var entity: HashMap<Any, ManfredComponent>? = entities[id]

        if (entity == null) {
            return null
        }

        return entity[compClass] as T?
    }


    fun finish() {

        for (id in _deleteList) {
            entities.remove(id)
        }

        _deleteList.clear()
    }


    fun getEntitiesWith(vararg compClasses: Class<*>): HashSet<Long> {

        var result = HashSet<Long>()

        //############# BEGIN Find all entities that have the specified components ###########
        for (entry in entities) {

            var allIn = true

            for (compClass in compClasses) {

                if (!entry.value.containsKey(compClass)) {
                    allIn = false;
                    break;
                }
            }

            if (allIn) {
                result.add(entry.key)
            }
        }
        //############# END Find all entities that have the specified components ###########

        return result
    }


    fun <T> removeComponent(id: Long, compClass: Class<T>) {

        var entity = entities[id]
        if (entity == null) return

        entity.remove(compClass)

        if (entity.isEmpty()) {
            _deleteList.add(id)
        }
    }


    fun removeEntity(id: Long) {

        var entity = entities.get(id)

        if (entity == null) return

        entity.clear()

        _deleteList.add(id)
    }


    fun setComponent(id: Long, comp: ManfredComponent) {

        var entity = entities[id];

        if (entity == null) {
            entity = HashMap()
            entities.set(id, entity)
        }

        comp.id = id

        entity.set(comp::class.java, comp)
    }
}