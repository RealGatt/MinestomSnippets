# MinestomSnippets
Random snippets of code I've written up while working on Minestom projects.

This repository is filled with individual classes/snippets of code. They may or may not have explanations, and hopefully I've grouped them up.

## ⚠️ Warning ⚠️
Do not ask me for help in implementing this code into your own project. It is expected to break. Most of my code is written for a single purpose, and not for wide-spread use- so timing efficiency isn't my highest priority. It just has to *work*.


# Groups

## Inventories
### [Chest Inventories](/Inventories/Chest%20Inventories/)
Last Updated: 03/Jan/2022

Includes:
> ChestFirstOpenEvent.java

A custom event that fires whenever a chest is opened for the first time (by someone who isn't a spectator)

> ChestHandler.java

My handler for Chests. Supports Double Chests and Single Chests. Will load existing data from the chests' NBT `Items` tag into the inventory, however- does not handle saving that data in any sorts. 


## Block Handlers

### [General Block Handlers](/Block%20Handlers/)
Last Updated: 16/Jan/2022

Includes
> FireHandler.java

> SignHandler.java

> OpenableHandler.java

> RegisterHandlers.java

Example handlers for different types of blocks.