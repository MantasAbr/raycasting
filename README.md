# Raycasting Engine 
This project has been created as a way to familiarize myself with the concepts of 3D rendering and certain game engine design aspects.
# Fog
World surfaces fade exponentially with full 3D distance from the camera,
including height. There is no fixed fog start or end distance. Adjust
`FOG_DENSITY` and `FOG_COLOR` (RGB hex) in `Raycasting.java` before starting the
game. Density is per map tile; increase it for thicker fog or use `0` to disable
fading. For colour, `0x000000` gives darkness, `0x808080` gives grey fog, and
`0x182028` gives blue-grey mist. Equal-fog contours form circles on horizontal surfaces in world space;
their size depends on surface height, and perspective changes their appearance.
Rendering uses a cached exponential lookup with at most one RGB level of error
per channel, and skips floor/ceiling pixels hidden behind walls.

# Thin interior walls
Keep the existing tile grid and append an optional `WALLS` section. For example,
the divider in `src/levels/source/02.txt` is now:

```text
WALLS 2
6 1 6 4 4 0.125
6 5 6 8 4 0.125
```

Each line is `x1 y1 x2 y2 textureId thickness`, in tile units. These two segments
lie along the boundary at `x = 6`, use texture 4, are one-eighth of a tile thick,
and leave a one-tile doorway between `y = 4` and `y = 5`. Set the grid tiles
occupied by the old interior blocks to `0` to free their floor space.

Coordinates follow the engine's `map[x][y]` convention: X is the row index in
the text file and Y is the column index. Integer coordinates describe tile
edges; fractional coordinates can position a partition inside a tile instead.
Segments must be horizontal or vertical, have nonzero length, and have a
thickness greater than 0 and at most 1. They use the same full-height wall
textures as block walls, including their narrow end faces. Doors are still
interactive grid tiles; thin partitions are static geometry.

The renderer indexes segments by the tiles they cross and tests only those
encountered by each ray. Movement uses the same bounds, with swept checks to
prevent stepping across a narrow wall. Maps without a `WALLS` section still work.

# Sources
* [Lode's Computer Graphics Tutorial](https://lodev.org/cgtutor/index.html)
* [Wikipedia article on Raycasting](https://en.wikipedia.org/wiki/Ray_casting)
* [The Cherno Youtube tutorials](https://www.youtube.com/playlist?list=PL656DADE0DA25ADBB)
* And a bunch of StackOverflow articles...
# Progress updates
You can track my progress on this [Trello board](https://trello.com/b/WJw6PoRG/raycasting)
# Progress images
* v0.1

![v0.1](https://github.com/MantasAbr/raycasting/blob/master/progress/v0.1.png)
# Future plans
Build up the engine to a state that I am comfortable with and start the development of a game (still not sure what it will be).
