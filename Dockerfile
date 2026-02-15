# Multi-stage Docker build for safe download container
FROM python:3.9-slim AS base

# Set working directory
WORKDIR /app

# Copy requirements file first to leverage docker layer caching
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Create non-root user for security
RUN adduser --disabled-password --gecos '' appuser && \
    chown -R appuser:appuser /app
USER appuser

# Expose port if needed (though this is primarily a download container)
EXPOSE 8000

# Default command to run the application
CMD ["python", "android_ops.py"]

# Multi-stage build for production image
FROM base AS production

# Install additional security tools
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        && rm -rf /var/lib/apt/lists/*

# Create directory structure for downloads
RUN mkdir -p /downloads

# Set environment variables
ENV PYTHONPATH=/app
ENV PATH="/usr/local/bin:${PATH}"

# Final production image
FROM base AS final

# Copy the built application from previous stage
COPY --from=production /app /app
COPY --from=production /usr/local/bin /usr/local/bin

# Create a directory for downloads
RUN mkdir -p /downloads

# Set working directory and user
WORKDIR /app
USER appuser

# Health check endpoint (if needed)
HEALTHCHECK CMD curl -f http://localhost:8000/health || exit 1

# Default command to run the application
CMD ["python", "android_ops.py"]
