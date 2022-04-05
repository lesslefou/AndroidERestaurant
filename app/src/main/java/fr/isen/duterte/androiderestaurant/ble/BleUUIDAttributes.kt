package fr.isen.duterte.androiderestaurant.ble

enum class BleUUIDAttributes(val uuid: String, val title: String) {
    GENERIC_ACCESS("00001800-0000-1000-8000-00805f9b34fb", "Accès générique"),
    GENERIC_ATTRIBUTE("00001801-0000-1000-8000-00805f9b34fb", "Attribut générique"),
    CUSTOM_SERVICE("466c1234-f593-11e8-8eb2-f2801f1b9fd1", "Service spécifique"),
    DEVICE_NAME("00002a00-0000-1000-8000-00805f9b34fb", "Nom du périphérique"),
    APPEARANCE("00002a01-0000-1000-8000-00805f9b34fb", "Apparance"),
    CUSTOM_CHARACTERISTIC("466c5678-f593-11e8-8eb2-f2801f1b9fd1", "Caracteristique spécifique"),
    CUSTOM_CHARACTERISTIC_2("466c9abc-f593-11e8-8eb2-f2801f1b9fd1", "Caracteristique spécifique"),
    HEART_RATE("0000180d-0000-1000-8000-00805f9b34fb", "Rythme Cardiaque"),
    UNKNOWN_SERVICE("", "Inconnu");

    companion object {
        fun getBLEAttributeFromUUID(uuid: String) = values().firstOrNull { it.uuid == uuid } ?: UNKNOWN_SERVICE
    }
}