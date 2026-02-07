# Contributing to Pix

Thank you for considering contributing to Pix Image Picker! This guide will help you get started.

## Code of Conduct

Please review our [Code of Conduct](../../CODE_OF_CONDUCT.md) before contributing.

**In summary:**
- Be respectful and inclusive
- Welcome diverse perspectives
- Focus on constructive feedback
- Report violations to maintainers

## Ways to Contribute

### 1. Report Bugs

Found a bug? Help us fix it!

**How to report:**
1. Check [existing issues](https://github.com/akshay2211/PixImagePicker/issues) first
2. Create a new issue with:
   - Clear title
   - Description of problem
   - Steps to reproduce
   - Expected vs. actual behavior
   - Device/Android version
   - Code example (if applicable)

**Good bug report:**
```
Title: Camera not working on Samsung Galaxy S21

Description:
When I try to capture a photo with front camera, it crashes.

Steps:
1. Open Pix with Mode.Picture
2. Tap camera icon
3. Tap front camera toggle
4. Try to take photo

Expected: Photo is captured
Actual: App crashes with ANR

Device: Samsung Galaxy S21
Android: 13
```

### 2. Suggest Features

Have an idea to improve Pix?

**How to suggest:**
1. Check [existing issues/discussions](https://github.com/akshay2211/PixImagePicker/discussions)
2. Create a discussion or issue with:
   - Clear title
   - Why you need this
   - Use case
   - Proposed solution (if any)

**Good feature request:**
```
Title: Support for image cropping

Use case:
Many apps require users to crop profile pictures before uploading.

Proposal:
Add optional cropping screen after image selection with:
- Drag corners to adjust
- Pinch to zoom
- Preset aspect ratios
```

### 3. Improve Documentation

Help make Pix easier to understand!

**What needs help:**
- Typos and clarity improvements
- Additional examples
- Troubleshooting guides
- Translations
- Video tutorials

**How to contribute:**
1. Fork the repo
2. Edit documentation in `/docs`
3. Test your changes locally with MkDocs
4. Submit PR with clear description

### 4. Contribute Code

Ready to code? We'd love your help!

#### Getting Started

1. **Fork the repository**
   ```bash
   gh repo fork akshay2211/PixImagePicker --clone
   cd PixImagePicker
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or for bugs:
   git checkout -b fix/bug-description
   ```

3. **Set up development environment**
   ```bash
   # Open in Android Studio
   # Wait for Gradle sync
   # Review code quality setup (Spotless, Detekt, etc.)
   ```

4. **Make your changes**
   - Write clean, readable code
   - Follow Android best practices
   - Add comments for complex logic
   - Write tests for new features

5. **Run quality checks**
   ```bash
   # Format code
   ./gradlew :pix:spotlessApply

   # Static analysis
   ./gradlew :pix:detekt

   # Run tests
   ./gradlew :pix:test

   # Build
   ./gradlew :pix:build
   ```

6. **Commit with clear messages**
   ```bash
   git commit -m "Add: feature description

   - Bullet point explaining change
   - Why this change was needed
   - Any important notes"
   ```

7. **Push and create PR**
   ```bash
   git push origin feature/your-feature-name
   # Then create PR on GitHub
   ```

#### Code Quality Standards

**Follow these standards:**

- **Kotlin style** - Uses ktlint (via Spotless)
- **Best practices** - Detekt static analysis
- **Testing** - Unit tests for new code
- **Documentation** - KDoc for public APIs
- **Performance** - Profile changes if performance-critical

**Code format:**
```kotlin
// Good
class MyClass {
    fun doSomething(): String {
        return "result"
    }
}

// Bad (will fail spotlessCheck)
class MyClass{
    fun doSomething( ) : String{
        return "result"
    }
}
```

#### Testing

All new features should include tests:

```kotlin
// Example test structure
class PixFragmentTest {
    @Test
    fun testFragmentCreation() {
        val options = Options().apply { count = 5 }
        val fragment = pixFragment(options) { }

        assertNotNull(fragment)
    }

    @Test
    fun testResultCallback() {
        // Test callback mechanism
    }
}
```

**Run tests:**
```bash
./gradlew :pix:test
```

#### Documentation

Update docs for new features:

1. **Code documentation** - KDoc comments
   ```kotlin
   /**
    * Creates a Pix picker fragment with the specified options.
    *
    * @param options Configuration options for the picker
    * @param callback Called when user makes selection
    * @return Configured PixFragment instance
    */
   fun pixFragment(options: Options, callback: (Result) -> Unit): PixFragment
   ```

2. **User documentation** - Update relevant `.md` files
3. **Sample app** - Add example code in app module
4. **Changelog** - Note your contribution

### 5. Answer Questions

Help other developers in:
- [GitHub Discussions](https://github.com/akshay2211/PixImagePicker/discussions)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/piximagepicker)
- [Reddit r/androiddev](https://www.reddit.com/r/androiddev/)

### 6. Share Your Work

Tell the world about Pix:
- Write blog posts
- Create video tutorials
- Share on social media
- Contribute to open-source lists

## Pull Request Process

### Before Submitting

- [ ] Code follows project style (run `spotlessApply`)
- [ ] Static analysis passes (`detekt`)
- [ ] Tests pass locally (`./gradlew :pix:test`)
- [ ] Build succeeds (`./gradlew :pix:build`)
- [ ] Documentation updated (if needed)
- [ ] Changelog updated (if needed)

### PR Description

```markdown
## Description
Brief description of what this PR does.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Other (please describe)

## Related Issue
Fixes #issue_number

## Changes Made
- Change 1
- Change 2
- Change 3

## Testing
Describe testing done:
- Device: [e.g., Pixel 6 Pro]
- Android Version: [e.g., 13]
- Test steps: [e.g., reproduce issue from #123]

## Screenshots (if applicable)
[Add screenshots of UI changes]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Tests written/updated
- [ ] Documentation updated
- [ ] No new warnings generated
```

### Review Process

1. **Maintainer reviews** your PR
2. **Feedback** provided (if any changes needed)
3. **Address feedback** with new commits
4. **Approval** once all concerns resolved
5. **Merge** into master branch

**Timeline:** Usually 3-7 days for review

### After Merge

- Your code is part of Pix!
- You'll be credited in changelog
- Thanks for helping the community

## Contributor Recognition

We recognize and thank contributors:
- **GitHub:** Shown in contributor list
- **Changelog:** Listed in version notes
- **Hall of Fame:** Featured on documentation

## Questions?

- **GitHub Issues** - Technical questions
- **GitHub Discussions** - Feature/design discussions
- **Email** - For private matters
- **Twitter** - For quick contact

## Development Resources

### Understanding the Codebase

Key modules:
- `pix/` - Main library
  - `core/` - Core functionality
  - `ui/` - UI components (Fragments, etc.)
  - `data/` - Data access layer
- `app/` - Sample application

### Important Classes

- `Pix` - Main entry point
- `Options` - Configuration
- `PixFragment` - Main UI fragment
- `PixBus` - Event bus for results
- `PixEventCallback` - Result callback interface

### Technology Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **Database:** N/A (uses media store)
- **Image Loading:** Glide
- **Camera:** CameraX
- **Threading:** Coroutines
- **UI:** AndroidX Fragments
- **Build:** Gradle with Kotlin DSL

## Git Workflow

```bash
# Keep your fork updated
git fetch upstream
git rebase upstream/master

# Before PR, ensure clean history
git rebase -i HEAD~3  # squash if needed

# Push to your fork
git push origin feature/your-feature
```

## Common Issues

### PR blocked by tests
- Run `./gradlew :pix:test` locally
- Check test logs for failures
- Fix issues and push new commits

### Merge conflicts
- Sync with master: `git fetch upstream && git rebase upstream/master`
- Resolve conflicts manually
- Run tests again
- Push resolved version

### CI pipeline failure
- Check GitHub Actions logs
- Common causes: formatting, tests, build
- Run local checks before pushing

## Tips for Successful Contributions

1. **Start small** - Begin with small PRs to learn the process
2. **Communicate** - Discuss large changes before coding
3. **Test thoroughly** - Test on multiple devices/API levels
4. **Follow conventions** - Match existing code style
5. **Be patient** - Maintainers volunteer their time
6. **Ask for help** - Don't hesitate to ask questions
7. **Read existing code** - Learn from similar implementations

## Resources

- **[Kotlin Style Guide](https://kotlinlang.org/docs/reference/coding-conventions.html)**
- **[Android Architecture](https://developer.android.com/jetpack)**
- **[Git Documentation](https://git-scm.com/doc)**
- **[GitHub Guides](https://guides.github.com/)**

## Thank You!

Contributing to Pix helps:
- ✅ Thousands of developers worldwide
- ✅ Improve Android development ecosystem
- ✅ Create better user experiences
- ✅ Build something meaningful

We truly appreciate your contribution, no matter how small!

---

Ready to contribute? [Start here](https://github.com/akshay2211/PixImagePicker/blob/master/CONTRIBUTING.md)