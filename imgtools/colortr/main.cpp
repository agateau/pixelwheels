#include <QCoreApplication>
#include <QColor>
#include <QCommandLineParser>
#include <QImage>

#include <iostream>

static void colorize(QImage& image, const QColor& src_, const QColor& dst_)
{
    const QRgb src = src_.rgba();
    const QRgb dst = dst_.rgba();
    for (int y = 0; y < image.height(); ++y) {
        QRgb* ptr = reinterpret_cast<QRgb*>(image.scanLine(y));
        for (int x = 0; x < image.width(); ++x, ++ptr) {
            if (*ptr == src) {
                *ptr = dst;
            }
        }
    }
}

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    QCommandLineParser parser;
    parser.addHelpOption();
    parser.addOption({"src", "Source color", "COLOR"});
    parser.addOption({"dst", "Destination color", "COLOR"});
    parser.addPositionalArgument("input", "Input image");
    parser.addPositionalArgument("output", "Output image");
    parser.process(app);

    QColor src = QColor(parser.value("src"));
    QColor dst = QColor(parser.value("dst"));
    QString input = parser.positionalArguments().at(0);
    QString output = parser.positionalArguments().at(1);

    QImage image;
    if (!image.load(input)) {
        std::cerr << "Failed to load " << input.toStdString() << '\n';
        return 1;
    }
    image = image.convertToFormat(QImage::Format_ARGB32);


    colorize(image, src, dst);

    if (!image.save(output)) {
        std::cerr << "Failed to save " << output.toStdString() << '\n';
        return 1;
    }

    return 0;
}
