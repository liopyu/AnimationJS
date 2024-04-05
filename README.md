# Summary

The AnimationJS mod is an addon for KubeJS that allows scripters to trigger animations on both the server player object and the client player object seamlessly.

## Features:

- Ability to trigger animations on both server and client player objects
- Plays animations through the [playerAnimator](https://www.curseforge.com/minecraft/mc-mods/playeranimator) mod

## Usage:

To trigger animations, use the `.triggerAnimation()` method in your KubeJS scripts. Provide the name of the animation as a resource location.

By default, this mod comes with a simple waving animation in the assets>animationjs>player_animation folder. For more information on how to add custom animations through JSON, visit the official EmoteCraft wiki page [here](https://kosmx.gitbook.io/emotecraft/tutorial/custom-emotes).

```javascript
event.player.triggerAnimation("animationjs:waving")
```

## Planned For The Future:

- Optional integration with [EmoteCraft](https://www.curseforge.com/minecraft/mc-mods/emotecraft-forge) Animations
- [BendyLib](https://www.curseforge.com/minecraft/mc-mods/bendy-lib) Support
- Server-sided emote support
- Custom animation building

## Dependencies:

- KubeJS
- [playerAnimator mod](https://www.curseforge.com/minecraft/mc-mods/playeranimator)

## Community

Join our [Discord](https://discord.gg/lat) community to connect with other users, share your creations, and get help
with any questions or issues you encounter while using AnimationJS.
