/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.block.properties.PropertyBool;


/**
 * Marker interface for blocks that have a lit/unlit state.
 * Includes the obnoxious static field.
 */
public interface ILightableBlock
{
    PropertyBool LIT = PropertyBool.create("lit");
}
