# Surface Rule Additions

Natural Philosophy adds several new conditions and rules to the Minecraft surface rule system. Many of these are shortcuts rather than new content, to enable faster rule creation or easier interpretation of existing rules. Because of this, they may not serve every purpose right away.

Natural Philosophy adds two categories of content to surface rules:
1. Surface Conditions
2. Surface Rules

Surface conditions are used with the `minecraft:condition` rule to test specific circumstances, while surface _rules_ act like conditionals and serve as shortcuts to having multiple rules within a `minecraft:sequence` rule and needing to analyze multiple conditions in a row, often doing computation (such as noise values) multiple times.

## Surface Conditions

### `naturalphilosophy:cliff`

Used for identification of cliff faces, not including cave lips or the bases of cave lips. Is a general purpose condition for the identification of cliffs; should have no edge case behavior to speak of.

### `naturalphilosophy:flat` & `naturalphilosophy:flat_liquid`

Used for identifying where the ground is flat, for solid blocks or for liquids. The standard `naturalphilosophy:flat` condition will erronously trigger some on chunk borders due to the chunk-bound nature of surface rules. For rules where the flatness of the terrain is of utmost importance, use `naturalphilosophy:flat_liquid`, which will return false on all chunk borders guaranteed when above y63 (sea level) and when the block below is air.

### `naturalphilosophy:land_top_layer`

Used for when you only want to return on dry land and the top layer, for instance changing the top block. Is solely a shortcut condition.

### `naturalphilosophy:underwater`

Checks if a block is underwater and returns true; `shallow` boolean is used to determine if this is "shallow" water as determined by where dirt vs ocean gravel is placed in Vanilla oceans.

### `naturalphilosophy:cave_depth`

Checks if a block is beneath heightmap by `depth` blocks and is also not far enough under an overhang to be counted as "open air". This check is only a heuristic intended for removing the Vanilla grass-in-caves issue where grass will sometimes be placed in caves. Is not intended to be a foolproof check of whether a block is in a cave and `depth` or greater blocks below heightmap, use at your own discretion for other purposes.

### `naturalphilosophy:biome`

A copy of the Vanilla `minecraft:biome` condition that can also accept tags if provided in the format `#namespace:id`.

## Surface Rules

All naturalphilosophy surface rules have some sort of optional `default` value. Rules interacting with `BlockState` values have a `default_state` field, while rules interacting with other surface rules have a `default_rule` field. This field is always optional, and not having a `default` field will result in a "pass-through" to the next value down the chain.

### `naturalphilosophy:bilayer_fill`

Used to create a "dirt and grass" layer on top of the surface. `top_layer` is a singular rule for the "grass" portion. `sublayer` is a singular rule for the "dirt" portion.

This rule will only place above water unless `land` is set to false; if `land` is false it will only place underwater.

The `surface_offset` and `secondary_depth_range` values behave exactly how they do with the `minecraft:stone_depth` condition, however only apply to the `sublayer`, the `top_layer` will always be only the top layer of the ground. 

### Selector Rules

Selector rules are those which evaluate a single value (be it noise, random, height, etc) and then make a choice between `BlockState`s or surface rules based upon the result of that evaluation. All of these will take some sort of array for the values, and which value is selected will be based on a threshold from the top down (be it the highest noise value, height, etc) iterated.

The `cascade` boolean is available for rules which can fail at resolution: `naturalphilosophy:noise_threshold_selector` and `naturalphilosophy:height_threshold_selector`. When `cascade` is true, a failure for a rule to resolve will lead for the next rule in the list to resolve, rather than a short-circuit to the `default` value.

If no rule or state is selected, the `default` rule will be selected. 

For all rules except `stone_depth_threshold_selector`, the rule will have a `lower_[type]_thresholds` list. Each element in this list corresponds to one in the `ruleset` or `state_set` list. It is the _lower_ bound of the random, noise, or height value that the rule will execute for.

For instance, if you have an array of `[ 0.5, 0.3 ]` the first rule in the `ruleset` parameter will be evaluated down to a noise value of `0.5`. Then, between `0.5` and `0.3`, the second rule will be evaluated. Below a noise value of `0.3`, the `default_rule` parameter will be executed if it exists; otherwise the rule will pass through to the back.

### `naturalphilosophy:noise_threshold_selector`

The ideal use-case for the noise threshold selector is when you want to create multiple bands of blocks that move from one block at a node to another block at a sink. Think perhaps of a forest clearing - the node can be the clearing and there can be grass, and then podzol, and then coarse dirt ringed around it.

Noise selectors take a noise `ResourceLocation` to evaluate under the `noise` parameter.

The`lower_noise_thresholds` parameter takes a list of `Double` values. _Be Aware!_ This parameter is _not_ linear. While most values will be distributed between `1.0` and `-1.0`, not all values will be. Expect values to reach roughly between `[3.0, -3.0]`.

This rule has the `cascade` and `default_rule` parameters as described above.

### `naturalphilosophy:random_threshold_selector`

The ideal use-case for the random threshold selector is when you want to create a random mix of blocks. It is simple to write and one of the shortest rules, but **does not** take other surface rules as parameters, only `BlockState` objects.

Random selectors take a `String` random name to seed their positional random factory.

The `lower_random_thresholds` parameter takes a list of `Double` values. This parameter _is_ linear, and is bounded to be evaluated between `1.0` and `0.0` The random will not return outside of that, so higher values will do nothing.

This rule has the `default_state` parameter as described above.

### `naturalphilosophy:height_threshold_selector`

The ideal use-case for the height threshold selector is when you want to create multiple bands of height-dependent generation.

Height threshold selectors take many of the parameters of the `minecraft:y_above` condition, but do **not** take `VerticalAnchor` objects. They rather take absolute coordinate heights.

The `add_stone_depth` parameter has the height evaluated at the top block of this column, while `surface_depth_multiplier` is an integer used to scale the surface depth noise before it is added to the height value to evaluate.

This rule has the `default_rule` parameter as described above.

### `naturalphilosophy:stone_depth_threshold_selector`

This rule is slightly different than the other selector rules. This rule is used if you want to have a guaranteed different block layer on every block below the surface, and has fewer options because of it.

The `ruleset` for this rule will select the rule at the index of the depth below the surface. The first block at the surface (0) will select the first rule, the first block below the surface (1) will select the second rule, etc.

This rule has the `default_rule` parameter as described above.
