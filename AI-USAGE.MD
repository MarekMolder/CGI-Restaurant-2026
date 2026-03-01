## AI Usage

This project used AI as a development support tool, primarily to accelerate repetitive code generation and boilerplate creation.

### AI-assisted / AI-generated Components

The following classes were implemented or significantly assisted by AI:

- `BookingPdfServiceImpl`
- `EmailServiceImpl`
- `TheMealDBServiceImpl`

### Testing Strategy with AI Assistance

For test development, a **template-first approach** was used.  
I manually wrote representative test classes for each application layer to define structure, style, and expected coverage.  
Based on these manually written examples, AI was used to generate additional tests following the same pattern.

#### Manually Written Base Tests

These test classes were written entirely by me:

- **Domain / Entities**
    - `BookingTest`
    - `UserTest`

- **Mappers**
    - `BookingMapperTest`
    - `UserMapperTest`

- **Repository**
    - `BookingRepositoryTest`
    - `UserRepositoryTest`

- **Service**
    - `BookingServiceImplTest`

- **Controller**
    - `BookingControllerTest`
    - `AuthControllerTest`

### Responsibility & Validation

All architectural decisions, business logic, and final implementations are my own.  
AI was used strictly as a productivity tool and not as an autonomous decision-maker.