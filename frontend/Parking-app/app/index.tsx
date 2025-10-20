import { useRouter } from "expo-router";
import { View, TextInput, Button, StyleSheet } from "react-native";
import { useState } from "react";

export default function Login() {
    const router = useRouter();
    const [email, setEmail] = useState("");
    const [pwd, setPwd] = useState("");

    return (
        <View style={styles.container}>
            <TextInput
                placeholder="Email"
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
                style={styles.input}
            />
            <TextInput
                placeholder="Password"
                value={pwd}
                onChangeText={setPwd}
                secureTextEntry
                style={styles.input}
            />
            <Button title="Login" onPress={() => router.replace("../home")} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: "center", padding: 20, gap: 12 },
    input: { borderWidth: 1, padding: 8, borderRadius: 6 },
});
