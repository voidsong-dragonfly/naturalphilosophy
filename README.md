# Natural Philosophy

Natural Philosophy is a mod that aims to transform and rejuvenate how Minecraft biomes are decorated. It takes new blocks only used in a few biomes and new concepts in generation since old biomes such as forests were designed and applies them to all biomes in the game, putting variety and life into Minecraft's biomes.

Natural Philosophy is designed with a goal of using as few modded features as possible, to showcase the power of the systems Mojang implemented with the data-driven world generation updates. Features that are implemented via code are described below, after the inspiration mods heading. 

## Important Notes

Natural Philosophy does not change modded biomes and has no plans to change modded biomes at this time. This is in part due to the amount of work and customization that needs to go into a singular biome, and how most mods do not add enough decorations to sufficiently differentiate their biomes from Minecraft biomes.

Natural Philosophy also replaces many Vanilla generation objects with its own, more organized counterparts. Mods that modify things such as Vanilla's `minecraft:patch_taiga_grass` or similar may not be supported.

Mods which do minimal overhauls of Vanilla biomes are considered to be 'extraneous' or 'actively detrimental' and are listed below, after the suggested mods heading. These are _not_ suggested for use with Natural Philosophy.

## Gallery

## Suggested Mods

[Tectonic](https://modrinth.com/datapack/tectonic) is one of the most gorgeous terrain generators for current versions.  
[Improved Village Placement](https://modrinth.com/mod/improved-village-placement) helps with Vanilla's terrible habit of cliff villages.  
[Repurposed Structures](https://modrinth.com/mod/repurposed-structures-forge) adds some more Vanilla-style structures to your game without overdoing it.    
[Regrowth](https://modrinth.com/mod/regrowth) adds dynamicity to a normally static Vanilla world.

## Supplanted Mods

#### Natural Philosophy Implements These Mods

 - Simple Snowy Fix
 - Snow Under Trees
 - Stony Cliffs Are Cool
 - CliffFace
 - Cliffs

#### Natural Philoosphy Supplants These Mods


 - Cliff Under a Tree
 - Biome Makeover
 - Swampier Swamps
 - BetterDefaultBiomes
 - Unnamed Desert
 - Scorched
 - Geophilic & Geophilic Reforged
 - Arboria
 - Project: Vibrant Journeys
 - Better Trees
 - Wilder Wild

## Elaboration, Developer Perspective, & Inspiration

### On Biome Generation in Minecraft

Terrain generation in Minecraft has gone through so many states of change that trying to categorize all of them into neat boxes is a futile endeavour. Many updates have made sweeping changes to generation (1.7, 1.13, 1.18 among recent examples) but one thing that _can_ be concretely said is that the feel of the game between older versions, especially Alpha, and modern versions has changed drastically. 

Alpha has a certain charm to its worldgen that is lacking in modern versions - the bright green grass and trees and limited worldgen palette combined with soaring cliffs and strange formations is not only iconic, but has continued to inspire a lot of worldgen in later versions. Modern versions have added much, much more complexity on top of the slowly-eroding seed that Alpha worldgen provided, but despite over a decade of development, have not managed to unlock the potential that the expansions could have provided.

To put it simply: _Minecraft's biomes do not feel finished_. The oldest have almost the same amount of content they did in 1.0.0 in terms of decorations and style. What was once charming has, with new content but none of it in old biomes, become boring and uninspired. At any time in the last decade, more work could have been put in to bring old biomes into a cohesive set of content - and 1.19 was supposed to do just that, but did not.

Instead, a few new biomes have been added that _do_ spice up the world, some desperately needed (oceanic biomes) and some less so; and forests' largest change was the introduction of bees. Moss is only found in Lush Caves, despite being vegetation that survives in forests, taigas, and even on oceanic rocks.

### The Point of It All

Natural Philosophy aims to fix the lack of attention given to Minecraft's biomes. Take old biomes, some new ideas and gripes about how Minecraft biomes don't make sense within limited evolutionary constraints, and make something new out of them. Take all of these inspirations and transform old biomes into not necessarily new experiences, but at least visual delights. 

Jungle biomes are transformed into a bi-layered tropical rainforest, with noise-distributed bush clusters on the coarse dirt and podzol ground. Above you soars a canopy over 24 meters tall, with an understory just a few meters over your head. Sometimes you hack your way through with a trusty machete, and sometimes you can gaze through small gaps in the foliage to see the sun.

Sparse jungles and swamps are no longer expanses of mown grass, and instead vibrant mixtures of foliage: in sparse jungles, a mix of understory jungle trees and bamboo, in swamps a mixture of copses of water-tolerant swamp oaks among rushbeds and sheafs of wet, muddy grasses.

Atop windswept hills short, hardy grasses cling to the sides of slopes as stunted fir and pine trees huddle, the wind making sure that the flourishing vegetation down below cannot reach into this last holdout. Above, denuded mountains still see boulders wedged into their sides from glaciers, sentinels among cliffs of rock.

Rivers see clay, gravel, mud, and sand in great abundance. No longer do scattered patches gird their shores like the drops from a painter's brush, instead wide swaths of the river have had sediment deposited over aeons such that a clay pit can be dug from beneath their slow-moving curves.

Deserts and badlands are no longer flat expanses of sand with randomly-spread cacti, but have patches of hardy grasses growing admist other vegetation. Wooded badlands are full of short, hardy bushes preserving moisture against the dry desert heat and slowly transforming the canyon tops from terracotta to coarse dirt.

Natural Philosophy specifically takes biomes that have been underdeveloped by the game and makes them slightly more realistic, while moving away from a paradigm of flat, mown grass with the occasional higher tuft. Features before confined to one biome can be seen in many, and some blocks seldom-useful have been expanded to provide better scenery, such as azalea bushes across the understory of most forests.

## Inspirations

[Geophilic](https://modrinth.com/datapack/geophilic) is similar to Natural Philosophy but sticks to a Vanilla decoration style.    
[Project Vibrant Journeys](https://www.curseforge.com/minecraft/mc-mods/project-vibrant-journeys) is focused on adding block-based decoration features.    
[Stony Cliffs Are Cool](https://modrinth.com/datapack/stony-cliffs-are-cool) adds stone cliffs to steep surfaces on generation.    
[Simple Snowy Fix](https://modrinth.com/mod/simple-snowy-fix) adds snow spawning and ice spawning underneath trees.

Other inspirations come from too many biome mods to count, here and there. A selected list for a few features of note can be found in `ideas.txt` in the main directory.

Simple Snowy Fix and Stony Cliffs are Cool are integrated into Natural Philosophy and need not be included alongside.

## Code Features

**New Blocks**
 - Dune Grass
 - Rushes
 - Cattails

**New Features**
 - Archaeology blocks outside of structures via ArchaeologyBlockFeature    

**New Surface Rule Conditions**
 - `minecraft:cliff` for cliff generation
 - `minecraft:flat` for swamp generation

**Snow Fixes**
 - Snow generates under trees with `minecraft:freeze_top_layer`
