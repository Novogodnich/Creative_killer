# Server Creative Killer (Fabric)

**Server Creative Killer** — a lightweight, exclusively **server-side** mod for Minecraft 1.21.1 that adds a precision administration tool. It allows server operators to instantly eliminate troublemakers or test game mechanics while completely ignoring Creative Mode, immortality effects, and third-party modifications.

Since the mod operates on the server side (**Server-side only**), players **don't need to download anything** — they can join the server from a completely vanilla client.

---

## 🚀 Key Features

* **Full Creative Mode Bypass:** Standard Minecraft damage ignores players in Creative. This mod bypasses this limitation at the server packet level.
* **Ignoring Immortality Mods:** Instead of dealing standard damage, the code uses a hard entity removal method (`discard()`), which prevents totems, custom armor, or third-party "absolute invulnerability" effects from triggering.
* **Safety for Regular Players:** The elimination weapon works **strictly in the hands of server operators (OP)** with permission level `2` or higher. A regular player, even if they somehow obtain this item, will not be able to activate its property.
* **Vanilla Compatibility:** The mod does not register new items in the game registry, which eliminates client crashes. The logic is tied to a regular Netherite Sword with a specific name component.

---

## 🛠 Installation and Usage

1. Compile the project using the command `./gradlew clean build`.
2. Place the resulting `.jar` file from the `build/libs/` folder into your server's `mods` folder.
3. Restart the server.

### How to Obtain the Weapon

Join the server with operator rights and run the vanilla item-giving command (in versions 1.21+, the format of old NBT tags has been changed to a component system):

```text
/give @s netherite_sword[custom_name='Creative Killer'] 1
```
_After that, any hit with this sword on a player (in any game mode) will instantly send the target to the respawn screen_.
