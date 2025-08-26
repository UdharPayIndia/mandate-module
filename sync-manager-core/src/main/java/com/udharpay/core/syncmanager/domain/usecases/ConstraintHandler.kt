package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.kernel.kernelcommon.eventbus.EventBus
import com.udharpay.kernel.kernelcommon.register.Register
import com.udharpay.core.syncmanager.domain.enities.SyncConstraint
import com.udharpay.core.syncmanager.domain.enities.SyncEvent
import com.udharpay.kernel.kernelcommon.taskexecutor.TaskExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class ConstraintHandler(
    private val syncEventBus: EventBus<SyncEvent>,
    constraintObserverRegister: Register<String, Flow<Any>>,
    taskExecutor: TaskExecutor
) {
    private val currentConstraints = hashMapOf<String, Any>()

    init {
        constraintObserverRegister.getEntries().forEach { constraintObserver ->
            taskExecutor.executeInCurrent {
                constraintObserver.value.collect {
                    currentConstraints[constraintObserver.key] = it
                    syncEventBus.fire(SyncEvent.ConstraintChange(constraintObserver.key, it))
                }
            }
        }
    }

    fun isConstraintMet(syncConstraint: SyncConstraint): Boolean  {
        var isConstraintMatch = true
        syncConstraint.constraints.forEach {
            val currentConstraintResolver = syncConstraint.constraintResolvers[it.key]
            val currentConstraint = currentConstraints[it.key]
            isConstraintMatch = if (currentConstraintResolver == null || currentConstraint == null) {
                false
            } else {
                val expectedConstraint = it.value
                isConstraintMatch && currentConstraintResolver(currentConstraint, expectedConstraint)
            }
        }
        return isConstraintMatch
    }

}
