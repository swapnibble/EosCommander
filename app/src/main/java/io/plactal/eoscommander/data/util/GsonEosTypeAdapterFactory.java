package io.plactal.eoscommander.data.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import io.plactal.eoscommander.data.remote.model.types.TypeAccountName;
import io.plactal.eoscommander.data.remote.model.types.TypeActionName;
import io.plactal.eoscommander.data.remote.model.types.TypeAsset;
import io.plactal.eoscommander.data.remote.model.types.TypeName;
import io.plactal.eoscommander.data.remote.model.types.TypePermissionName;
import io.plactal.eoscommander.data.remote.model.types.TypeScopeName;

/**
 * Created by swapnibble on 2018-04-04.
 */

public class GsonEosTypeAdapterFactory implements TypeAdapterFactory {

    private Map<Class<?>, TypeAdapter<?>> adapters = new LinkedHashMap<>();

    {
        adapters.put(TypeName.class, new TypeNameAdapter<>( TypeName.class));
        adapters.put(TypeAccountName.class, new TypeNameAdapter<>( TypeAccountName.class));
        adapters.put(TypeActionName.class, new TypeNameAdapter<>( TypeActionName.class));
        adapters.put(TypePermissionName.class, new TypeNameAdapter<>( TypePermissionName.class));
        adapters.put(TypeScopeName.class, new TypeNameAdapter<>( TypeScopeName.class));

        adapters.put( TypeAsset.class, new TypeNameAdapter<>(TypeAsset.class));
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        TypeAdapter<T> typeAdapter = null;
        Class<?> currentType = Object.class;
        for (Class<?> type : adapters.keySet()) {
            if (type.isAssignableFrom(typeToken.getRawType())) {
                if (currentType.isAssignableFrom(type)) {
                    currentType = type;
                    typeAdapter = (TypeAdapter<T>)adapters.get(type);
                }
            }
        }
        return typeAdapter;
    }

    public static class TypeNameAdapter<C> extends TypeAdapter<C> {

        private Class<C> clazz;

        public TypeNameAdapter(Class<C> clazz){
            this.clazz = clazz;
        }

        @Override
        public C read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            try {
                Constructor<C> constructor = clazz.getConstructor( String.class );
                return constructor.newInstance( in.nextString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void write(JsonWriter out, C value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.value(value.toString());
        }
    }
}
