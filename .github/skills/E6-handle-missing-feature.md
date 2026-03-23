# Skill E6: Handle Missing Framework Feature

**When to use:** Source code uses a feature that doesn't exist in target framework

**Purpose:** Find equivalent functionality or implement alternative solution.

## Input

- Feature description (what source code does)
- Source framework feature being used
- Target framework
- Usage context

## Steps

### 1. Document the Missing Feature

Clearly identify:
- What feature is used in source
- What it does functionally
- Why it's needed
- How it's currently used
- Example usage from source code

### 2. Research Target Framework

**Check official documentation:**
- API reference
- Migration guides (especially "Source → Target" guides)
- Cookbook/recipes section
- Community forums

**Search for:**
- Direct equivalent
- Similar functionality
- Recommended patterns
- Third-party libraries that provide it

**Check version notes:**
- Was feature added in later version?
- Was it deprecated/removed?
- Is there a replacement?

### 3. Determine Solution Strategy

Choose appropriate approach:

**A. Direct Equivalent Exists**
- Use target framework's built-in feature
- Update code to use new API

**B. No Direct Equivalent - Can Polyfill**
- Feature can be implemented as utility function
- Create helper/polyfill

**C. No Equivalent - Architectural Change**
- Feature doesn't translate 1:1
- Need different approach in target
- Redesign that part of application

**D. No Equivalent - External Library**
- Community library provides functionality
- Install and integrate third-party package

**E. Feature Not Essential**
- Can be simplified or removed
- Refactor to not need it

### 4. Apply Solution

#### Solution A: Use Direct Equivalent

**Example: Layout Manager**

**Source (Java Swing):**
```java
panel.setLayout(new BorderLayout());
panel.add(component, BorderLayout.NORTH);
```

**Target (Angular + Flexbox):**
```html
<div class="container">
  <div class="north">{{ component }}</div>
</div>
```

```css
.container {
  display: flex;
  flex-direction: column;
}
```

**Map concepts:**
- BorderLayout → CSS Flexbox/Grid
- Layout constraints → CSS positioning
- Add with position → Template + CSS classes

#### Solution B: Create Polyfill/Helper

**Example: Observable Lifecycle**

**Source has feature X, target doesn't:**

**Create utility:**
```typescript
// utils/feature-polyfill.ts
export class FeatureHelper {
  static doFeature(input: string): string {
    // Implement the missing functionality
    return input.toUpperCase(); // Example
  }
}
```

**Use in migrated code:**
```typescript
import { FeatureHelper } from '../utils/feature-polyfill';

const result = FeatureHelper.doFeature(value);
```

#### Solution C: Architectural Alternative

**Example: Swing Event Listeners**

**Source (Java Swing):**
```java
button.addActionListener(e -> {
  System.out.println("Clicked");
});
```

**Target (Angular):**
```typescript
// Component template:
<button (click)="onButtonClick()">Click</button>

// Component class:
onButtonClick() {
  console.log("Clicked");
}
```

**Different pattern but same behavior.**

#### Solution D: Use Third-Party Library

**Example: Advanced Data Grid**

**Source:** Complex data table with built-in features

**Target:** Framework has basic table

**Solution:** Install grid library

```bash
npm install ag-grid-angular
```

```typescript
import { AgGridModule } from 'ag-grid-angular';

@Component({
  template: '<ag-grid-angular [rowData]="data"></ag-grid-angular>'
})
```

#### Solution E: Simplify/Remove

**Example: Complex Animation**

**Source:** Has sophisticated animation API

**Target:** Basic CSS transitions

**Evaluate:**
- Is complex animation essential?
- Can we use simple CSS transitions instead?
- Will users notice the simplification?

**If non-essential:**
```typescript
// Use simple CSS transition instead of complex animation
// Add class to trigger CSS transition
element.classList.add('fade-in');
```

### 5. Common Missing Features and Solutions

#### Desktop → Web Migrations

| Desktop Feature | Web Equivalent |
|----------------|----------------|
| File System Access | File API (with user permission) |
| Multi-window Apps | Browser tabs / Modal dialogs |
| System Tray | Browser notifications |
| Native Menus | HTML/CSS menus |
| Drag & Drop Files | HTML5 Drag & Drop API |
| Clipboard Access | Clipboard API |
| Print Dialog | window.print() + CSS print styles |

#### Framework-Specific Solutions

**Java → TypeScript:**
- `Thread.sleep()` → `await new Promise(r => setTimeout(r, ms))`
- `synchronized` → Not needed (single-threaded JS) or use async locks
- `File I/O` → Web: File API, Node: fs module
- `Reflection` → TypeScript has limited reflection, use decorators

**Swing → Web Framework:**
- `JFrame` → Page/Component root
- `JPanel` → `<div>` container
- `JButton` → `<button>` element
- `JTextField` → `<input type="text">`
- `JLabel` → `<span>` or `<label>`
- `Layout Managers` → CSS Flexbox/Grid

### 6. Implement and Test

After applying solution:

1. **Implement** the alternative approach
2. **Test** that behavior matches original
3. **Verify** edge cases work
4. **Check performance** if applicable
5. **Document** the deviation if significant

### 7. Document in Migration Report

Add note to `migration-report.md`:

```markdown
## Feature Substitutions

### BorderLayout → CSS Flexbox
- **Original:** Java Swing's BorderLayout manager
- **Replacement:** CSS Flexbox with directional classes
- **Behavior:** Equivalent visual layout
- **Limitation:** None

### File System Access → File API
- **Original:** Direct file system read/write
- **Replacement:** Browser File API with user permission
- **Behavior:** User must select files via dialog
- **Limitation:** Can't access arbitrary files without user consent
```

## Output

- Working implementation using target framework capabilities
- Documented substitution in migration report
- Preserved original functionality (or acceptable alternative)

## Examples

### Example 1: Swing Timer → RxJS/setTimeout

**Source:**
```java
Timer timer = new Timer(1000, e -> {
  updateUI();
});
timer.start();
```

**Target:**
```typescript
import { interval } from 'rxjs';

const subscription = interval(1000).subscribe(() => {
  this.updateUI();
});

// Later: subscription.unsubscribe();
```

**Or simpler:**
```typescript
setInterval(() => {
  this.updateUI();
}, 1000);
```

### Example 2: Dependency Injection Container

**Source (Spring):**
```java
@Autowired
private UserService userService;
```

**Target (Angular):**
```typescript
constructor(private userService: UserService) {}
```

**Same concept, different syntax.**

### Example 3: Thread-based Concurrency

**Source (Java):**
```java
new Thread(() -> {
  // Background work
  String result = doWork();
  SwingUtilities.invokeLater(() -> {
    updateUI(result);
  });
}).start();
```

**Target (TypeScript/Angular):**
```typescript
// Use async/await with promises
async performWork() {
  const result = await this.doWork();  // Async work
  this.updateUI(result);  // Update UI
}

// Or with RxJS:
this.http.get('/api/work').subscribe(result => {
  this.updateUI(result);
});
```

### Example 4: Missing Component - Install Library

**Source:** Has rich text editor built-in

**Target:** Framework has `<textarea>` only

**Solution:**
```bash
npm install @angular/cdk
npm install quill
npm install ngx-quill
```

```typescript
import { QuillModule } from 'ngx-quill';

@NgModule({
  imports: [QuillModule.forRoot()]
})

// In component:
<quill-editor [(ngModel)]="content"></quill-editor>
```

### Example 5: Simplification

**Source:** Complex custom renderer

**Target:** Use standard components

**Decision:** Original renderer had 20 custom features, but app only uses 3.

**Solution:** Implement only the 3 features that are actually used, ignore the rest.

```typescript
// Only implement what's needed
renderItem(item: Item) {
  // Original had 20 rendering modes
  // We only need 'text', 'image', 'link'
  switch (item.type) {
    case 'text': return this.renderText(item);
    case 'image': return this.renderImage(item);
    case 'link': return this.renderLink(item);
  }
}
```
