var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI')
var Opcodes = Java.type('org.objectweb.asm.Opcodes')
var InsnList = Java.type('org.objectweb.asm.tree.InsnList')
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode')
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode')
var LookupSwitchInsnNode = Java.type('org.objectweb.asm.tree.LookupSwitchInsnNode')

function initializeCoreMod() {
	return {
		'apoli_prevent_armor_equip': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraftforge.common.extensions.IForgeItemStack',
				'methodName': 'canEquip',
				'methodDesc': '(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;)Z'
			},
			'transformer': function(node) {
				//if (CoreUtils.isItemForbidden(this.self(), entity, slot)) return false;
				var ls = new InsnList();
				ls.add(new VarInsnNode(Opcodes.ALOAD, 2));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
				ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
				ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/edwinmindcraft/apoli/common/util/CoreUtils", "isItemForbidden", "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)Z"));
				var label = new LabelNode();
				ls.add(new JumpInsnNode(Opcodes.IFEQ, label));
				ls.add(new InsnNode(Opcodes.ICONST_0)); //false
				ls.add(new InsnNode(Opcodes.IRETURN));
				ls.add(label);
				node.instructions.insert(ls);
				return node;
			}
		},
		'apoli_override_food_properties': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraftforge.common.extensions.IForgeItemStack',
				'methodName': 'getFoodProperties',
				'methodDesc': '(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;'
			},
			'transformer': function(node) {
				//if (CoreUtils.isItemForbidden(this.self(), entity, slot)) return false;
				var ls = new InsnList();
				ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
				ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeItemStack", "self", "()Lnet/minecraft/world/item/ItemStack;", true));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
				ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/edwinmindcraft/apoli/common/util/CoreUtils", "transformFoodProperties", "(Lnet/minecraft/world/food/FoodProperties;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"));
				var iterator = node.instructions.iterator();
				var insertionSlot = null;
				while (iterator.hasNext()) {
					var ain = iterator.next();
					if (ain.getOpcode() === Opcodes.ARETURN)
						insertionSlot = ain;
				}
				if (insertionSlot != null)
					node.instructions.insertBefore(insertionSlot, ls);
				return node;
			}
		},
		'apoli_modify_friction': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraftforge.common.extensions.IForgeBlockState',
				'methodName': 'getFriction',
				'methodDesc': '(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F'
			},
			'transformer': function(node) {
				//return CoreUtils.modifyFriction(prev, reader, pos, entity, this.self());
				var ls = new InsnList();
				ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 2));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 3));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
				ls.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/common/extensions/IForgeBlockState", "self", "()Lnet/minecraft/world/level/block/state/BlockState;", true));
				ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/edwinmindcraft/apoli/common/util/CoreUtils", "modifyFriction", "(FLnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;)F", false));
				var iterator = node.instructions.iterator();
				var insertionSlot = null;
				while (iterator.hasNext()) {
					var ain = iterator.next();
					if (ain.getOpcode() === Opcodes.FRETURN)
						insertionSlot = ain;
				}
				if (insertionSlot != null)
					node.instructions.insertBefore(insertionSlot, ls);
				return node;
			}
		},
		'apoli_prevent_suffocation': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.world.entity.Entity'
			},
			'transformer': function(classNode) {
				var iter = classNode.methods.iterator()
				while (iter.hasNext()) {
					var node = iter.next();
					var methodName = ASMAPI.mapMethod('m_201940_');
					if (node.desc === "(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/core/BlockPos;)Z") {
						var gsc2 = ASMAPI.mapMethod("m_60812_");
						var gsc3 = ASMAPI.mapMethod("m_60742_");
						var ccof = ASMAPI.mapMethod("m_82750_");

						var target = ASMAPI.findFirstMethodCall(node, ASMAPI.MethodType.VIRTUAL, "net/minecraft/world/level/block/state/BlockState", gsc2, "(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;");
						var ls = new InsnList();
						ls.add(new VarInsnNode(Opcodes.ALOAD, 0));
						ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/world/phys/shapes/CollisionContext", ccof, "(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;", true));
						ls.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/level/block/state/BlockState", gsc3, "(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", false));
						if (target != null) {
							node.instructions.insertBefore(target, ls);
							node.instructions.remove(target);
						}
					}
				}
				return classNode;
			}
		},
		'apoli_modify_harvest_check': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraftforge.common.extensions.IForgeBlockState',
				'methodName': 'canHarvestBlock',
				'methodDesc': '(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z'
			},
			'transformer': function(node) {
				//switch(CoreUtils.allowHarvest(arg1, arg2, arg3)) case 0: return false; case 1: return true; default: <...>
				var labelFalse = new LabelNode();
				var labelTrue = new LabelNode();
				var labelNone = new LabelNode();
				var ls = new InsnList();
				ls.add(new VarInsnNode(Opcodes.ALOAD, 1));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 2));
				ls.add(new VarInsnNode(Opcodes.ALOAD, 3));
				ls.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/edwinmindcraft/apoli/common/util/CoreUtils", "allowHarvest", "(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)I", false));
				ls.add(new LookupSwitchInsnNode(labelNone, [0, 1], [labelFalse, labelTrue]));
				ls.add(labelFalse);
				ls.add(new InsnNode(Opcodes.ICONST_0));
				ls.add(new InsnNode(Opcodes.IRETURN));
				ls.add(labelTrue);
				ls.add(new InsnNode(Opcodes.ICONST_1));
				ls.add(new InsnNode(Opcodes.IRETURN));
				ls.add(labelNone);
				node.instructions.insert(ls);
				return node;
			}
		}
	}
}