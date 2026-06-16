# Smart Salon Queue & Appointment Management System

A full-stack web application that allows customers to view 
real-time salon waiting times, check barber availability, 
and book appointments online.

## Tech Stack
- Java Spring Boot
- MySQL Database
- Spring Data JPA
- Spring Security
- WebSocket

## Features
- Real-time queue tracking
- Barber availability management
- Appointment booking system
- Estimated wait time calculation

## API Endpoints
- GET /api/salons — All salons
- GET /api/salons/area/{area} — Salons by area
- POST /api/barbers — Add barber
- PATCH /api/barbers/{id}/status — Update barber status
- POST /api/bookings — Create booking
- GET /api/bookings/salon/{id}/queue — Live queue info
