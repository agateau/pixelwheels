import os

from aseprite import AsepriteImage


def load_fixture(name):
    return AsepriteImage(os.path.join("fixtures", name))


def test_layer_group_order():
    image = load_fixture("layer-groups.ase")
    # Layer groups in this image form a hierarchy like this:
    #
    # g2
    #   2.1
    #   g2.2
    #       2.2.2
    #       2.2.1
    # g1
    #   1.1
    layers = image.layers
    assert len(layers) == 7
    # Groups first, then their children. Image layers from bottom to top
    expected_layer_names = ["g1", "1.1", "g2", "g2.2", "2.2.1", "2.2.2", "2.1"]
    assert [x.name for x in layers] == expected_layer_names


def test_layer_group_parents():
    image = load_fixture("layer-groups.ase")

    def layer_by_name(name):
        for layer in image.layers:
            if layer.name == name:
                return layer
        assert False, "No layer named {}".format(name)

    g2 = layer_by_name("g2")
    g1 = layer_by_name("g1")
    g22 = layer_by_name("g2.2")
    layer221 = layer_by_name("2.2.1")

    assert g2.parent is None
    assert g1.parent is None
    assert g22.parent is g2
    assert layer221.parent is g22


def test_layer_visibility():
    image = load_fixture("layer-visibility.ase")
    # Layers in this image look like this:
    #
    # 4 (hidden)
    # 3
    # g2 (hidden)
    #   2.1
    # g1
    #   1.1 (hidden)

    layers = image.layers
    assert len(layers) == 6

    def layer_by_name(name):
        for layer in image.layers:
            if layer.name == name:
                return layer
        assert False, "No layer named {}".format(name)

    assert layer_by_name("g1").visible
    assert not layer_by_name("1.1").visible

    # Because its parent is not visible
    assert not layer_by_name("2.1").is_really_visible()
    assert layer_by_name("2.1").visible

    assert not layer_by_name("g2").visible
    assert layer_by_name("3").visible
    assert not layer_by_name("4").visible
