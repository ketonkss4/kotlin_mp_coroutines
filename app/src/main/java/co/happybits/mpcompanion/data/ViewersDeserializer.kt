package co.happybits.mpcompanion.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ViewersDeserializer : JsonDeserializer<Viewers>{
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Viewers {
        val jsonObject = json.asJsonObject
        val keySet = jsonObject.keySet()
        return Viewers(ArrayList(keySet))
    }

}
