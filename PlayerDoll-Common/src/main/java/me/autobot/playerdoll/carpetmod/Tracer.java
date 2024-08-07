package me.autobot.playerdoll.carpetmod;

public class Tracer {
//
//    private static final Field ENTITY_FIELD;
//    private static final Field NMS_ENTITY_LEVEL_FIELD;
//    private static final Class<?> NMS_ENTITY_CLASS;
//
////    private static final Method isSpectatorMethod;
//    //private static final Method getPickRadiusMethod;
////    private static final Method isPickableMethod;
////    private static final Method getRootVehicleMethod;
//
////    private static final Method levelGetEntitiesMethod;
//
////    private static final Method getViewVectorMethod;
////    private static final Method getEyePositionMethod;
//    /*
//    private static final Method vec3ScaleMethod;
//    private static final Method vec3DistanceToSqrMethod;
//    private static final Method vec3AddDoubleMethod;
//    private static final Method vec3AddVec3Method;
//    private static final List<Field> vec3CoordsFieldList;
//
//     */
//
//    private static final Constructor<?> clipContextConstructor;
//    private static final Class<?> levelClass;
//    private static final Method levelClipMethod;
//
//
//
//    //private static final Method getLocationMethod;
//
//    private static final Enum<?> clipBlockEnumOutline;
//    private static final Enum<?> clipFluidEnumNone;
//    private static final Enum<?> clipFluidEnumAny;
//
//    private static final Field entityBoundingBoxField;
//    /*
//    private static final Method boundingBoxExpandTowardsMethod;
//    private static final Method boundingBoxInflateMethod;
//    private static final Method boundingBoxClipMethod;
//    private static final Method boundingBoxContainsMethod;
//
//     */
//
//    private static final Class<?> entityHitResultClass;
//    private static final Constructor<?> entityHitResultConstructor;
//
//    static {
//        ENTITY_FIELD = Arrays.stream(ReflectionUtil.getCBClass("entity.CraftEntity").getDeclaredFields())
//                .filter(field -> field.getName().equals("entity"))
//                .findFirst()
//                .orElseThrow();
//        ENTITY_FIELD.setAccessible(true);
//
//        // NMS ENTITY CLASS
//        NMS_ENTITY_CLASS = ENTITY_FIELD.getType();
//
////        getPickRadiusMethod = Arrays.stream(NMS_ENTITY_CLASS.getMethods())
////                .filter(method -> Modifier.isPublic(method.getModifiers()))
////                .filter(method -> method.getReturnType() == float.class && method.getParameterCount() == 0)
////                // uncertain
////                .sorted(Comparator.comparing(Method::getName))
////                .toList().get(0);
//
////        getRootVehicleMethod = Arrays.stream(NMS_ENTITY_CLASS.getMethods())
////                .filter(method -> Modifier.isPublic(method.getModifiers()))
////                .filter(method -> method.getReturnType() == NMS_ENTITY_CLASS && method.getParameterCount() == 0)
////                .sorted(Comparator.comparing(Method::getName))
////                .toList().get(1);
//
//
//        // Vec3 (mojang) / Vec3D
////        Class<?> vec3Class = ReflectionUtil.getClass("net.minecraft.world.phys.Vec3D");
//        levelClass = ReflectionUtil.getClass("net.minecraft.world.level.World");
//        NMS_ENTITY_LEVEL_FIELD = Arrays.stream(NMS_ENTITY_CLASS.getDeclaredFields())
//                // Level (mojang) / World
//                .filter(field -> field.getType() == levelClass)
//                .findFirst()
//                .orElseThrow();
//        NMS_ENTITY_LEVEL_FIELD.setAccessible(true);
//
////        List<Method> entityGetVec3List = Arrays.stream(NMS_ENTITY_CLASS.getMethods())
////                .filter(method -> Modifier.isFinal(method.getModifiers()))
////                .filter(method -> method.getReturnType() == vec3Class && method.getParameterCount() == 1 && method.getParameterTypes()[0] == float.class)
////                .sorted(Comparator.comparing(Method::getName))
////                .toList();
////
////        getViewVectorMethod = entityGetVec3List.get(0);
////        getEyePositionMethod = entityGetVec3List.get(2);
//
//        // BlockHitResult (mojang) / MovingObjectPositionBlock
//        Class<?> blockHitResultClass = ReflectionUtil.getClass("net.minecraft.world.phys.MovingObjectPositionBlock");
//        // BlockGetter (mojang) / IBlockAccess
//        Class<?> blockGetterClass = ReflectionUtil.getClass("net.minecraft.world.level.IBlockAccess");
//        Objects.requireNonNull(blockGetterClass, "blockGetterClass");
//        // ClipContext (mojang) / RayTrace
//        final String clipContextClassName = "net.minecraft.world.level.RayTrace";
//        Class<?> clipContextClass = ReflectionUtil.getClass(clipContextClassName);
//        Class<?> clipContextFluidEnumClass = ReflectionUtil.getClass(clipContextClassName + "$FluidCollisionOption");
//        Class<?> clipContextBlockEnumClass = ReflectionUtil.getClass(clipContextClassName + "$BlockCollisionOption");
//
//        Objects.requireNonNull(clipContextClass, "clipContextClass");
//        Objects.requireNonNull(clipContextFluidEnumClass, "clipContextFluidEnumClass");
//        Objects.requireNonNull(clipContextBlockEnumClass, "clipContextBlockEnumClass");
//
//        clipContextConstructor = Arrays.stream(clipContextClass.getConstructors())
//                .filter(constructor -> constructor.getParameterCount() == 5 && constructor.getParameterTypes()[4] == NMS_ENTITY_CLASS)
//                .findFirst()
//                .orElseThrow();
//
//        levelClipMethod = Arrays.stream(blockGetterClass.getDeclaredMethods())
//                .filter(method -> method.getReturnType() == blockHitResultClass && method.getParameterCount() == 1 && method.getParameterTypes()[0] == clipContextClass)
//                .findFirst()
//                .orElseThrow();
//        levelClipMethod.setAccessible(true);
//
//        try {
//            Enum<?>[] blockEnum = (Enum<?>[]) clipContextBlockEnumClass.getMethod("values").invoke(null);
//            clipBlockEnumOutline = blockEnum[1];
//            Enum<?>[] fluidEnum = (Enum<?>[]) clipContextFluidEnumClass.getMethod("values").invoke(null);
//            clipFluidEnumNone = fluidEnum[0];
//            clipFluidEnumAny = fluidEnum[2];
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        // AABB (mojang) / AxisAlignedBB
//        Class<?> boundingBoxClass = ReflectionUtil.getClass("net.minecraft.world.phys.AxisAlignedBB");
//
//        entityBoundingBoxField = Arrays.stream(NMS_ENTITY_CLASS.getDeclaredFields())
//                .filter(field -> !Modifier.isStatic(field.getModifiers()))
//                .filter(field -> field.getType() == boundingBoxClass)
//                .findFirst()
//                .orElseThrow();
//        entityBoundingBoxField.setAccessible(true);
//
//        entityHitResultClass = ReflectionUtil.getClass("net.minecraft.world.phys.MovingObjectPositionEntity");
//        Objects.requireNonNull(entityHitResultClass, "entityHitResultClass");
//
//        // EntityHitResult (mojang) / MovingObjectPositionEntity
//        entityHitResultConstructor = Arrays.stream(entityHitResultClass.getConstructors())
//                .filter(constructor -> constructor.getParameterCount() == 2)
//                .findFirst()
//                .orElseThrow();
//
//    }
//
//    public static WrapperHitResult rayTrace(ActionPackPlayer source, float partialTicks, double reach, boolean fluids)
//    {
//        Object nmsEntity = source.toServerPlayer();
//        WrapperBlockHitResult blockHit = rayTraceBlocks(nmsEntity, partialTicks, reach, fluids);
//        double maxSqDist = reach * reach;
//        if (blockHit.getSource() != null)
//        {
//            maxSqDist = blockHit.getLocation().distanceToSqr(WrapperVec3.wrap(getEyePosition(nmsEntity, partialTicks)));
//        }
//        WrapperHitResult entityHit = rayTraceEntities(source, partialTicks, reach, maxSqDist);
//        return entityHit == null ? blockHit : entityHit;
//    }
//
//    public static WrapperBlockHitResult rayTraceBlocks(Object source, float partialTicks, double reach, boolean fluids)
//    {
//        WrapperVec3 pos = new WrapperVec3(getEyePosition(source, partialTicks));
//        WrapperVec3 rotation = new WrapperVec3(getViewVector(source, partialTicks));
//        WrapperVec3 reachEnd = new WrapperVec3(pos.add(rotation.x * reach, rotation.y * reach, rotation.z * reach));
//        return new WrapperBlockHitResult(getLevelClip(source, newClipContext(pos.getSource(), reachEnd.getSource(), clipBlockEnumOutline, fluids ?
//                clipFluidEnumAny : clipFluidEnumNone, source)));
//    }
//
//    public static WrapperEntityHitResult rayTraceEntities(ActionPackPlayer source, float partialTicks, double reach, double maxSqDist)
//    {
//        Object nmsEntity = source.toServerPlayer();
//        WrapperVec3 pos = new WrapperVec3(getEyePosition(nmsEntity, partialTicks));
//        WrapperVec3 reachVec = WrapperVec3.wrap(new WrapperVec3(getViewVector(nmsEntity, partialTicks)).scale(reach));
//        WrapperAABB box = WrapperAABB.wrap(WrapperAABB.wrap(new WrapperAABB(nmsEntityGetBoundingBox(nmsEntity)).expandTowards(reachVec)).inflate(1));
//        return rayTraceEntities(source, pos.getSource(), pos.add(reachVec), box.getSource(), maxSqDist);
//    }
//
//    public static WrapperEntityHitResult rayTraceEntities(ActionPackPlayer source, Vec3D start, Vec3D end, AxisAlignedBB box, double maxSqDistance)
//    {
//        double targetDistance = maxSqDistance;
//        Object target = null;
//        WrapperVec3 targetHitPos = null;
//        for (Object current : source.getEntities(box))
//        {
//            WrapperAABB currentBox = WrapperAABB.wrap(new WrapperAABB(nmsEntityGetBoundingBox(current)).inflate(getPickRadius(current)));
//            Optional<Vec3D> currentHit = currentBox.clip(WrapperVec3.wrap(start), WrapperVec3.wrap(end));
//            if (currentBox.contains(WrapperVec3.wrap(start)))
//            {
//                if (targetDistance >= 0)
//                {
//                    target = current;
//                    targetHitPos = WrapperVec3.wrap(currentHit.orElse(start));
//                    targetDistance = 0;
//                }
//            }
//            else if (currentHit.isPresent())
//            {
//                WrapperVec3 currentHitPos = WrapperVec3.wrap(currentHit.get());
//                double currentDistance = new WrapperVec3(start).distanceToSqr(currentHitPos);
//                if (currentDistance < targetDistance || targetDistance == 0)
//                {
//                    if (getRootVehicle(current) == getRootVehicle(source.toServerPlayer()))
//                    {
//                        if (targetDistance == 0)
//                        {
//                            target = current;
//                            targetHitPos = currentHitPos;
//                        }
//                    }
//                    else
//                    {
//                        target = current;
//                        targetHitPos = currentHitPos;
//                        targetDistance = currentDistance;
//                    }
//                }
//            }
//        }
//        return target == null ? null : new WrapperEntityHitResult(entityHitResultNewInstance(target, targetHitPos.getSource()));
//    }
//
//
//    private static float getPickRadius(Object nmsEntity) {
//        return WrapperEntity.invokeNMS(nmsEntity).getPickRadius();
////        try {
////            return (float) getPickRadiusMethod.invoke(nmsEntity);
////        } catch (InvocationTargetException | IllegalAccessException e) {
////            throw new RuntimeException(e);
////        }
//    }
//    private static Object getRootVehicle(Object nmsEntity) {
//        return WrapperEntity.invokeNMS(nmsEntity).getRootVehicle();
//    }
//    private static Object entityGetLevel(Object nmsEntity) {
//        return ReflectionUtil.getField(levelClass, NMS_ENTITY_LEVEL_FIELD, nmsEntity);
//    }
//
//    @SuppressWarnings("SameParameterValue")
//    private static Object newClipContext(Object vec3, Object vec3d, Enum<?> raytrace_blockcollisionoption, Enum<?> raytrace_fluidcollisionoption, Object nmsEntity) {
//        return ReflectionUtil.newInstance(clipContextConstructor, vec3, vec3d, raytrace_blockcollisionoption, raytrace_fluidcollisionoption, nmsEntity);
//    }
//    private static Object getLevelClip(Object nmsEntity, Object clipContext) {
//        return ReflectionUtil.invokeMethod(levelClipMethod, entityGetLevel(nmsEntity), clipContext);
//    }
//
//    private static Object getViewVector(Object nmsEntity, float f) {
//        return WrapperEntity.invokeNMS(nmsEntity).getViewVector(f);
//    }
//    private static Vec3D getEyePosition(Object nmsEntity, float f) {
//        return (Vec3D) WrapperEntity.invokeNMS(nmsEntity).getEyePosition(f);
//    }
//
//    private static Object nmsEntityGetBoundingBox(Object nmsEntity) {
//        return ReflectionUtil.getField(entityBoundingBoxField, nmsEntity);
//    }
//
//    private static Object entityHitResultNewInstance(Object nmsEntity, Object vec3) {
//        return ReflectionUtil.newInstance(entityHitResultConstructor, nmsEntity, vec3);
//    }
}
