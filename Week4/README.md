## Week 4

Simple PoC of a Download Manager.

| command | description |
| --- | --- |
| `make build` | Compile `*.java` files to `*.class` |
| `make run-t` | Run the multi-threaded version (needs a url arg) |
| `make run-p` | Run the multi-process version (needs a url arg) |

Example:
```bash
make build
make run-t url=https://download.virtualbox.org/virtualbox/7.0.14/VirtualBox-7.0.14-161095-OSX.dmg
make run-p url=https://download.virtualbox.org/virtualbox/7.0.14/VirtualBox-7.0.14-161095-OSX.dmg
```
