# UPGRADE PLAN

## Overview
This document outlines the plan to upgrade the vcam module with improved stealth capabilities, performance optimizations, and modern Android framework integration based on intelligence gathering.

## 1. LSposed Scope Implementation (MainHook)

### Current State Analysis
The module currently lacks proper scope declaration which is required for LSPosed compatibility. According to LSPosed documentation, modules need meta-data declarations in AndroidManifest.xml to define their scopes properly.

### Required Changes
- Modify `AndroidManifest.xml` to include proper `xposedmodule` meta-data tags with scope definitions
- Update the MainHook class to check `lpparam.packageName` for proper module activation
- Implement package-specific logic based on scope requirements

## 2. Performance Optimization (VideoInjector)

### Current State Analysis
The VideoInjector currently uses MediaPlayer which can introduce latency and performance issues, especially under heavy load or with high-resolution videos.

### Required Changes
- Replace MediaPlayer implementation with MediaCodec for better performance
- Integrate Surface-based input/output for more efficient video processing
- Implement proper buffer management to reduce lag

## 3. Stealth Implementation (Zygisk Detection Bypass)

### Current State Analysis
The module needs improved stealth capabilities to avoid detection by Zygisk-based monitoring systems.

### Required Changes
- Add proper module description hiding techniques
- Implement anti-detection measures for Magisk/Zygisk environments
- Ensure the module doesn't expose itself through typical Xposed patterns

## Implementation Details

### MainHook Updates:
1. Add scope meta-data to AndroidManifest.xml
2. Check lpparam.packageName in hook initialization 
3. Implement package-specific activation logic

### VideoInjector Updates:
1. Replace MediaPlayer with MediaCodec
2. Use Surface-based processing instead of MediaPlayer's internal handling
3. Optimize buffer management for real-time video injection

### Stealth Enhancements:
1. Hide module description from system queries
2. Implement Zygisk detection bypass patterns
3. Ensure minimal footprint in system processes
