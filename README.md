# A demo application for Minecraft development

A demonstration of applying a fully reactive, non-blocking stack with hexagonal architecture in a Minecraft environment

This project showcases how reactive programming (Reactor + Spring) can be applied to a Minecraft server environment while keeping the hexagonal architecture (ports & adapters) principles.
The domain remains pure and independent of Reactor types, while adapters integrate with Redis Pub/Sub, PostgreSQL (R2DBC), and Netty pipeline injection to build a fully non-blocking system.
