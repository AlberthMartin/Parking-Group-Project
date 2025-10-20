import { Stack } from "expo-router";

export default function RootLayout() {
    return (
        <Stack>
            {/* index.tsx will be your Login screen */}
            <Stack.Screen name="index" options={{ title: "Login" }} />
            <Stack.Screen name="home/index" options={{ title: "Home" }} />
        </Stack>
    );
}
