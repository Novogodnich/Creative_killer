# Server Creative Killer (Fabric)

**Server Creative Killer** is a **server-side** mod for Minecraft 1.21.11 and 26.3.
It adds an administration tool. The tool allows server operators to instantly eliminate troublemakers or test game mechanics while completely ignoring Creative Mode, immortality effects, and third-party modifications.

Since this is a server mod, **players don't need to download anything**.

---

## 🚀 Key Features

* **Full Creative Mode Bypass:** Standard Minecraft damage ignores players in Creative. This mod bypasses this limitation at the server level.
* **Ignoring Immortality Mods:** Instead of dealing standard damage, the code uses a hard entity removal method (`discard()`), which prevents totems, custom armor, or third-party "absolute invulnerability" effects from triggering.
* **Safety for Regular Players:** By default, the elimination weapon works **strictly in the hands of server operators (OP)**. A regular player, even if they somehow obtain this item, cannot activate its property without the required permissions.
* **Permission API Support:** Server administrators can grant specific permissions to regular players, allowing them to use Creative Killer without making them operators.
* **Vanilla Compatibility:** The mod does not register new items in the game registry, which eliminates client crashes. The logic is tied to a regular Netherite Sword with a specific name component.
* **Configurable "Kill With Any Item" Mode:** An optional server-side mode lets operators (or players with the special permission) kill entities with **any item**, without needing the Creative Killer sword.
* **Configurable OP Bypass:** Server operators can be forced to rely on permissions instead of their OP status — useful for testing permission setups or for servers where OP is granted broadly.

---

## 🛠 Installation and Usage

You can just download it, but if you want hardcore...

1. Compile the project using the command `./gradlew clean build`.
2. Place the resulting `.jar` file from the `build/libs/` folder into your server's `mods` folder.
3. Restart the server.

### How to Obtain the Weapon

Join the server with operator rights and run the vanilla item-giving command:

```text
/give @s netherite_sword[custom_name='Creative Killer'] 1
```

After that, hitting an entity with this sword will activate Creative Killer, provided you have the required permissions.

### "Kill With Any Item" Mode

If `active_all_mode` is enabled in the config, any item held by a permitted player becomes a Creative Killer. No special sword is required — a regular hit with anything (a stick, a block, a fist) will instantly eliminate the target.

---

## ⚙️ Configuration

The config file is located at `config/creative_killer.json` and is created automatically on first launch. If the file is missing or invalid, defaults are used.

| Option | Default | Description |
|---|---|---|
| `active_all_mode` | `false` | When `true`, **any item** held by a permitted player acts as a Creative Killer. When `false`, only the Netherite Sword named `Creative Killer` works. |
| `permissions_for_ops_needed` | `true` | When `true`, **OPs must also have the permission** to use Creative Killer — OP status alone is not enough. When `false`, OPs bypass permission checks (classic behavior). |

### Example config

```json
{
  "active_all_mode": false,
  "permissions_for_ops_needed": true
}
```

---

## 🔑 Permissions

Creative Killer supports the Fabric Permissions API.

Server administrators can grant permissions to regular players, allowing them to use the weapon without granting OP status.

| Permission | Description |
|---|---|
| `creative-killer.kill` | Allows killing all entities, including players and mobs. |
| `creative-killer.kill.player` | Allows killing players. |
| `creative-killer.kill.mob` | Allows killing mobs. |
| `creative_killer.killWithAnyItem` | Allows using **any item** as a Creative Killer when `active_all_mode` is enabled. |

The general permission grants access to both player and mob targets. The specific permissions grant access only to their respective target types.

The `creative_killer.killWithAnyItem` permission is only relevant in `active_all_mode` and is checked separately from the sword permissions.

**OPs retain access to Creative Killer** (unless `permissions_for_ops_needed` is set to `true`). Regular players need to have permission to activate it.

---

### Support me
*I have enough money to live, but if you want more updates, you can support me in LTC:
`LPdWTvdipEGiYhHCdwnUpga7gR22kvLUFo`*