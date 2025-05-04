# Rules for Effective AI-Assisted Coding

## Core Principles

1. **Human Oversight Remains Essential**
   - AI is a tool, not a replacement for human judgment
   - Always review and understand AI-generated code before implementation
   - Maintain final decision-making authority on all code changes

2. **Verify Correctness**
   - Test all AI-generated code thoroughly
   - Never assume AI-generated code is bug-free or optimal
   - Validate edge cases that AI might have missed

3. **Understand Before Implementing**
   - Don't implement code you don't understand
   - Ask the AI to explain complex sections if needed
   - Use AI as a learning tool, not just a code generator

## Practical Guidelines

4. **Provide Clear Context**
   - Give AI sufficient context about your project and requirements
   - Include relevant existing code, constraints, and edge cases
   - Specify language, framework, and version requirements

5. **Iterative Refinement**
   - Start with a basic prompt and refine iteratively
   - Break complex tasks into smaller, manageable chunks
   - Use feedback loops to improve AI-generated solutions

6. **Specific Prompting**
   - Be specific about what you need
   - Include expected inputs, outputs, and behavior
   - Specify performance requirements and constraints
   - Mention error handling expectations

7. **Code Style Consistency**
   - Specify your project's coding style and conventions
   - Ask AI to follow existing patterns in your codebase
   - Ensure generated code maintains consistent naming conventions
   - Always include a newline at the end of text files (Java, Markdown, etc.) to avoid "No newline at end of file" warnings

## Security and Quality

8. **Security First**
   - Explicitly request secure coding practices
   - Review AI-generated code for security vulnerabilities
   - Never accept code with hardcoded credentials or insecure practices

9. **Performance Awareness**
   - Specify performance requirements upfront
   - Review for inefficient algorithms or resource usage
   - Ask for optimizations when necessary

10. **Documentation Requirements**
    - Request appropriate comments and documentation
    - Ensure complex logic is well-explained
    - Ask for usage examples for functions and classes

## Ethical Considerations

11. **Respect Intellectual Property**
    - Be cautious about code that seems copied from known sources
    - Verify licensing compatibility with your project
    - Understand the implications of using AI-generated code in your licensing

12. **Bias and Fairness**
    - Review code for potential biases in algorithms
    - Consider accessibility and inclusivity in UI/UX code
    - Ensure AI-generated solutions work for diverse user groups

13. **Transparency**
    - Document where and how AI was used in your development process
    - Be transparent with stakeholders about AI usage
    - Maintain clear attribution of work

## Workflow Integration

14. **Version Control Practices**
    - Commit AI-generated code in small, reviewable chunks
    - Document AI assistance in commit messages when appropriate
    - Use branches for experimental AI-generated solutions

15. **Code Review Process**
    - Apply extra scrutiny to AI-generated code during reviews
    - Have team members review AI-generated code
    - Use automated tools to supplement manual reviews

16. **Continuous Learning**
    - Document effective prompting strategies
    - Share successful AI interactions with your team
    - Continuously refine your approach to working with AI

## Technical Debt Management

17. **Refactoring Responsibility**
    - Don't accept overly complex solutions just because they work
    - Request simplifications when code is unnecessarily complex
    - Plan for refactoring AI-generated code as understanding improves

18. **Dependency Management**
    - Scrutinize AI-suggested dependencies carefully
    - Verify compatibility with your existing ecosystem
    - Consider long-term maintenance implications

19. **Testing Strategy**
    - Request tests along with implementation code
    - Ensure test coverage for critical paths
    - Verify that tests are meaningful and not just superficial

## Continuous Improvement

20. **Feedback Loop**
    - Provide feedback to improve AI responses
    - Document what works and what doesn't
    - Refine prompts based on previous interactions

21. **Stay Updated**
    - Keep aware of AI capabilities and limitations
    - Update your practices as AI tools evolve
    - Share knowledge about effective AI usage with your team

## Android-Specific Guidelines

22. **Activity Lifecycle**
    - Handle all lifecycle events properly (onCreate, onPause, onResume, etc.)
    - Save and restore state during configuration changes
    - Properly manage resources during lifecycle transitions

23. **UI/UX for Mobile**
    - Design for touch interaction with appropriate target sizes
    - Support multiple screen sizes and orientations
    - Follow Material Design guidelines for consistent user experience

24. **Resource Management**
    - Optimize assets for mobile (size, format, resolution)
    - Implement proper memory management to prevent leaks
    - Use appropriate caching strategies for network resources

25. **Permission Handling**
    - Request only permissions that are absolutely necessary
    - Implement runtime permission requests with proper explanation
    - Gracefully handle cases where permissions are denied

26. **Background Processing**
    - Use appropriate components for background work (WorkManager, Services)
    - Minimize battery impact with efficient background operations
    - Handle process death and app state restoration properly

27. **Testing on Real Devices**
    - Test on multiple physical devices with different specs
    - Don't rely solely on emulator testing
    - Test under various conditions (low battery, poor network, etc.)

28. **Asset Management**
    - Always run the asset synchronization script after modifying any assets
    - Use the watch_assets.sh script when making multiple asset changes
    - Verify assets appear correctly in the Android environment
    - Follow the established asset naming conventions and directory structure

29. **Performance Optimization**
    - Use Android Profiler to identify bottlenecks
    - Implement efficient layouts (ConstraintLayout, etc.)
    - Minimize work on the main thread
    - Use ViewBinding or DataBinding for efficient view access

30. **Fragment Management**
    - Follow the single-activity architecture when appropriate
    - Handle fragment lifecycle events properly
    - Use the Navigation component for fragment transitions
    - Implement proper communication between fragments

31. **Data Storage**
    - Choose appropriate storage solutions (Room, SharedPreferences, etc.)
    - Implement proper data migration strategies
    - Secure sensitive data with encryption
    - Follow best practices for database design

32. **Networking**
    - Implement proper error handling for network requests
    - Use efficient libraries (Retrofit, OkHttp, etc.)
    - Cache network responses appropriately
    - Handle network state changes gracefully

33. **Gradle Configuration**
    - Organize build.gradle files efficiently
    - Use BuildConfig for environment-specific variables
    - Implement proper dependency management
    - Optimize build times with appropriate configurations

34. **Accessibility**
    - Implement content descriptions for UI elements
    - Support screen readers and other accessibility services
    - Test with accessibility tools (TalkBack, etc.)
    - Follow Android accessibility guidelines