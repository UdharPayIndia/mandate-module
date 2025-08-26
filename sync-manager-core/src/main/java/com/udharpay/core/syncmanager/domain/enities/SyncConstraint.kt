package com.udharpay.core.syncmanager.domain.enities

class SyncConstraint private constructor(val constraints: HashMap<String, Any>, val constraintResolvers: HashMap<String, (Any, Any) -> Boolean>) {

    class Builder {
        private val constraints: HashMap<String, Any> = hashMapOf()
        private val constraintResolvers: HashMap<String, (Any, Any) -> Boolean> = hashMapOf()

        constructor()

        constructor(syncConstraint: SyncConstraint) {
            constraints.putAll(syncConstraint.constraints)
            constraints.putAll(constraintResolvers)
        }

        fun setConstraint(key: String, value: Any, constraintResolver: (Any, Any) -> Boolean) = apply {
            constraints[key] = value
            constraintResolvers[key] = constraintResolver
        }

        fun build(): SyncConstraint {
            return SyncConstraint(constraints, constraintResolvers)
        }
    }
}
