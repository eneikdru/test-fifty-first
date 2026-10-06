# ADR-003 - Georgian Voice AI (STT) Spike

## Context
As part of the initiative to support hands-free voice booking for drivers, we need an engine to transcribe spoken Georgian (`ka` / `ka-GE`) to text. The project brief requires processing voice requests to match against service types and available timeslots. We must evaluate existing Speech-to-Text (STT) providers to ensure they support the Georgian language with an acceptable Word Error Rate (WER) before integrating them into the production architecture.

## Decision
We conducted a spike to identify available APIs supporting the Georgian language:
- **OpenAI Whisper:** Supports Georgian (`ka`) natively in its multilingual models (`tiny`, `base`, `small`, etc.). As an open-source model, it can be hosted locally or accessed via API (e.g., Groq, OpenAI).
- **Google Cloud Speech-to-Text:** Supports Georgian (`ka-GE`) via its REST/gRPC API.

**Next Steps / Handoff:**
We recommend proceeding with a practical Word Error Rate (WER) test using domain-specific audio (booking a master).
The **BARCAN-TAG-10 (Integration/Development)** role will take ownership of the next slice: implementing a script to send a reference Georgian audio file to both APIs and logging the exact WER.

## Consequences
- No new infrastructure or service dependencies are added yet.
- The project explicitly defers the final STT engine selection until exact error rates on domain-specific vocabulary are documented.
