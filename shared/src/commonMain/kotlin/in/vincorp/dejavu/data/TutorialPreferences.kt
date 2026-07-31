package in.vincorp.dejavu.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TutorialPreferences(
    private val settings: Settings = Settings()
) {
    var isFirstTime: Boolean
        get() = settings.getBoolean(KEY_FIRST_TIME, true)
        set(value) = settings.set(KEY_FIRST_TIME, value)

    fun markTutorialSeen() {
        isFirstTime = false
    }

    companion object {
        private const val KEY_FIRST_TIME = "first"
    }
}
