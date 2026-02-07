# Roadmap

Planned features, improvements, and the future direction of Pix Image Picker.

## Version History

### v1.6.8 (Current - February 2025)

**Released:** February 2025

**Features:**
- ✅ Material 3 design updates
- ✅ Android 14+ support
- ✅ Improved scoped storage handling
- ✅ Performance optimizations
- ✅ CameraX 1.3+ support
- ✅ Enhanced theme customization

**Improvements:**
- Better orientation handling
- Faster image loading
- Reduced memory footprint
- Fixed known issues with specific devices

---

## v1.7.0 (Q2 2025)

### Planned Features

**1. Jetpack Compose Support**
```kotlin
// Compose API coming soon
@Composable
fun PixPicker(options: Options, onResult: (Result) -> Unit) {
    // Composable version of Pix
}
```

**2. Advanced Filtering**
- Filter by date range
- Filter by size range
- Filter by media type
- Custom filter implementations

**3. Multiple Selection Modes**
- Range selection
- Album-based selection
- Intelligent selection suggestions

**4. Enhanced Video Support**
- Thumbnail preview in video timeline
- Duration indicator in gallery
- Video information display

**Status:** In Development

### Improvements
- Better performance with very large galleries (5000+ items)
- Improved dark mode support
- Better orientation transition handling

---

## v1.8.0 (Q4 2025)

### Major Features

**1. Kotlin Multiplatform (KMP) Support**

Making Pix available on multiple platforms:
```kotlin
// Expected in v1.8
// Android support (already there)
// iOS support (via KMP)
// Desktop support (via KMP)
```

**Current Status:** In Planning
- iOS implementation
- Shared business logic
- Platform-specific UI

**Benefits:**
- Code sharing across platforms
- Consistent API across Android, iOS, Web
- Reduces duplication
- Easier maintenance

**2. Cropping & Editing**
- Basic image cropping
- Rotation support
- Built-in crop UI
- Customizable crop ratios

**3. Web Version (React)**
- JavaScript/React wrapper
- Web picker functionality
- File upload support

---

## v2.0.0 (2026)

### Major Redesign

**Architecture Update:**
- Modern MVVM with Jetpack Compose
- Reactive state management
- Improved separation of concerns

**Platform Expansion:**
- iOS support via KMP
- Web support via React
- Desktop support

**Feature Expansion:**
- Advanced image/video editing
- Cloud storage integration
- Multiple source support (Photos, Drive, OneDrive)

---

## Features in Backlog

### Short-term (Next 6 months)

| Feature | Priority | Effort |
|---------|----------|--------|
| Compose support | High | 2 weeks |
| Advanced filtering | High | 1 week |
| Better video support | High | 3 days |
| Album view | Medium | 1 week |
| Batch edit metadata | Medium | 2 weeks |

### Medium-term (6-12 months)

| Feature | Priority | Effort |
|---------|----------|--------|
| KMP support | High | 4 weeks |
| Image cropping | Medium | 2 weeks |
| Cloud integration | Medium | 3 weeks |
| iOS version | Medium | 8 weeks |

### Long-term (1-2 years)

| Feature | Priority | Effort |
|---------|----------|--------|
| Desktop app | Low | 6 weeks |
| Web version | Low | 4 weeks |
| Advanced editing | Low | 8 weeks |
| AI features | Low | TBD |

---

## Detailed Upcoming Features

### Jetpack Compose Support (v1.7)

**Why this matters:**
- Modern Android development standard
- Better performance
- Easier to write and maintain
- Future-proof

**Current status:** Design phase

**Timeline:** Q2 2025

### Advanced Filtering (v1.7)

```kotlin
// Proposed API
Options().apply {
    filters = listOf(
        DateRangeFilter(from, to),
        SizeRangeFilter(minSize, maxSize),
        MediaTypeFilter(Mode.Picture),
        CustomFilter { uri -> include(uri) }
    )
}
```

**Benefits:**
- Users find media faster
- Reduce large selections
- Better UX for specific use cases

**Timeline:** Q2 2025

### KMP Support (v1.8)

**Phase 1:** Shared business logic
- Selection logic
- Configuration handling
- Result processing

**Phase 2:** iOS implementation
- Native iOS UI
- Camera integration
- Permission handling

**Phase 3:** Multi-platform API
- Consistent API across platforms
- Shared models
- Platform-specific implementations

**Timeline:** Q4 2025 - Q1 2026

### Image Cropping (v1.8)

```kotlin
// Crop images after selection
Options().apply {
    enableCropping = true
    cropAspectRatios = listOf(
        Ratio.RATIO_4_3,
        Ratio.RATIO_16_9,
        Ratio.RATIO_1_1
    )
}
```

**Benefits:**
- One-step crop and pick
- Better UX
- Reduce app complexity

**Timeline:** Q4 2025

---

## Not Currently Planned

The following features are **not planned** for Pix (at least not in the near future):

1. **Advanced Image Editing**
   - Heavy editing is outside Pix's scope
   - Use dedicated editing libraries instead
   - Pix focus: selection, not editing

2. **Social Media Integration**
   - Sharing is app-specific
   - Pix focuses on media selection
   - Use sharing APIs separately

3. **Built-in Cloud Sync**
   - Sync is complex and app-specific
   - Better handled by apps themselves
   - Pix focus: local media selection

4. **Machine Learning Features**
   - Not relevant to all users
   - Better as optional integration
   - Future possibility

---

## How to Request Features

Want a feature not listed above?

1. **Check existing issues** on [GitHub](https://github.com/akshay2211/PixImagePicker/issues)
2. **Create new issue** with:
   - Clear description
   - Use case
   - Proposed API (if possible)
   - Why you think others need it

3. **Discuss in community**
   - GitHub Discussions
   - Stack Overflow
   - Reddit (r/androiddev)

4. **Contribute**
   - Submit PR for small features
   - Discuss larger changes first
   - See [Contributing Guide](contributing.md)

---

## Development Process

### How Features Get Added

1. **Proposal** - Community discusses need
2. **Design** - Team designs API and approach
3. **Development** - Feature is implemented
4. **Testing** - Thorough testing across devices
5. **Documentation** - Docs and examples updated
6. **Release** - Feature shipped in minor/major version

### Timeline Transparency

- Current work: Posted on GitHub Projects
- Planned features: Listed on roadmap (this page)
- In progress: Tagged in GitHub issues
- Completed: Released in version

### How to Track Progress

1. **GitHub Issues** - Feature discussions and tracking
2. **GitHub Projects** - Visual progress board
3. **GitHub Milestones** - Version planning
4. **Releases** - Completed features
5. **Changelog** - What changed in each version

---

## Version Release Schedule

### Current Release Cadence

- **Major versions** (v1.x → v2.0): Every 12-18 months
- **Minor versions** (v1.6 → v1.7): Every 3-4 months
- **Patch versions** (v1.6.0 → v1.6.8): Every 2-4 weeks
- **Hotfixes**: As needed for critical bugs

### Support Policy

- **Current version:** Full support
- **Previous minor version:** Bug fixes and critical updates
- **Older versions:** Critical security fixes only

### Deprecation Policy

Deprecations are announced 2+ versions in advance:

```kotlin
@Deprecated("Use newFeature instead", ReplaceWith("newFeature"))
fun oldFeature() { }
```

---

## Contributing to Pix

Want to help shape Pix's future?

### How to Contribute

1. **Feedback** - Report issues and suggest features
2. **Testing** - Test pre-release versions
3. **Documentation** - Improve docs and examples
4. **Code** - Submit PRs for bugs and features
5. **Community** - Help others in discussions

See [Contributing Guide](contributing.md) for details.

---

## Questions About the Roadmap?

- **GitHub Issues** - Technical questions
- **GitHub Discussions** - Feature discussions
- **Twitter** - For quick questions
- **Email** - For urgent matters

---

## Summary

Pix is actively developed and evolving. Key upcoming improvements:

| When | What |
|------|------|
| **Q2 2025** | Compose support, advanced filtering |
| **Q4 2025** | KMP support, image cropping |
| **2026** | iOS version, multi-platform support |

**Status:** On track for all scheduled deliverables

---

Next: [Learn how to contribute](contributing.md)