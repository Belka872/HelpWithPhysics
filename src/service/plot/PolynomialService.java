package service.plot;

public class PolynomialService {
    public float[] getPolynomial(float[] xs, float[] ys) {
        int degree = xs.length;
        float[][] deltas = new float[degree][degree];
        float[] result = new float[degree];

        for (int i = 0; i < degree; i++) {
            deltas[i] = getDeltaPolynomial(xs, i);
        }

        for (int i = 0; i < degree; i++) {
            result = addPolynomials(result, scalePoly(deltas[i], ys[i]));
        }

        return result;
    }

    public String formatPolynomial(float[] coefs) {
        StringBuilder builder = new StringBuilder();
        int degree = coefs.length;
        for (int i = 0; i < degree; i++) {
            float coef = coefs[i];
            if (coef > 0 && builder.length() > 0) {
                builder.append("  +  ");
            }
            if (coef < 0) {
                coef *= -1;
                builder.append("  -  ");
            }
            if (coef != 0) {
                if (coef == (int) coef) {
                    if (coef != 1 || (coef == 1 && degree - i - 1 == 0)) {
                        builder.append((int) coef);
                    }
                } else {
                    builder.append(coef);
                }
            }
            if (coef != 0) {
                if (degree - i - 1 == 1) {
                    builder.append("x");
                } else if (degree - i - 1 > 1) {
                    builder.append("x^").append(degree - i - 1);
                }
            }
        }
        return "f(x) = " + builder;
    }

    private float[] multiplyPolynomials(float[] a, float[] b) {
        int degA = a.length;
        int degB = b.length;
        float[] result = new float[degA + degB - 1];
        for (int i = 0; i < degA; i++) {
            for (int j = 0; j < degB; j++) {
                result[i + j] += a[i] * b[j];
            }
        }
        return result;
    }

    private float[] addPolynomials(float[] a, float[] b) {
        int degree = a.length;
        float[] c = new float[degree];
        for (int i = 0; i < degree; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    private float[] getDeltaPolynomial(float[] xs, int xpos) {
        float[] poly = {1};
        float denom = 1;
        for (int i = 0; i < xs.length; i++) {
            if (i != xpos) {
                float[] currentTerm = {1, -xs[i]};
                denom *= xs[xpos] - xs[i];
                poly = multiplyPolynomials(poly, currentTerm);
            }
        }
        return scalePoly(poly, 1 / denom);
    }

    private float[] scalePoly(float[] a, float k) {
        float[] b = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i] * k;
        }
        return b;
    }
}
